package eu.europa.ec.jrc.lca.registry.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;
import eu.europa.ec.jrc.lca.registry.dao.GenericDAO;

public abstract class GenericDAOImpl<T, ID extends Serializable> implements
		GenericDAO<T, ID> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericDAOImpl.class);

	@PersistenceContext
	private EntityManager manager;

	private Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public GenericDAOImpl() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected Class<T> getPersistentClass() {
		return persistentClass;
	}

	@Override
	public T findById(ID id) {
		return manager.find(persistentClass, id);
	}

	@Override
	public List<T> findAll() {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(persistentClass);
		TypedQuery<T> q = getEntityManager().createQuery(query);
		return q.getResultList();
	}

	@Override
	public T saveOrUpdate(T entity) {
		if (getIdValue(entity) == null) {
			manager.persist(entity);
			return entity;
		} else {
			return manager.merge(entity);
		}
	}

	protected String getIdPropertyName() {
		return getIdPropertyName(getPersistentClass());
	}

	private String getIdPropertyName(Class<?> clazz) {
		for (Field f : clazz.getDeclaredFields()) {
			if (f.getAnnotation(Id.class) != null) {
				return f.getName();
			}
		}
		if (clazz.getSuperclass() != null) {
			return getIdPropertyName(clazz.getSuperclass());
		}
		return null;
	}

	private ID getIdValue(T entity) {
		Field[] fields = persistentClass.getDeclaredFields();
		for (Field currentField : fields) {
			if (currentField.isAnnotationPresent(javax.persistence.Id.class)){
				try {
					currentField.setAccessible(true);
					ID id = (ID) currentField.get(entity);
					currentField.setAccessible(false);
					return id;
				} catch (IllegalArgumentException e) {
					LOGGER.error("[getIdValue]", e);
				} catch (IllegalAccessException e) {
					LOGGER.error("[getIdValue]", e);
				} 
			}
		}
		return null;
	}
	
	@Override
	public List<T> find(SearchParameters sp) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(persistentClass);
        
        if(sp!=null){
        	Root<T> root = query.from(persistentClass);
			List<Predicate> predicates = buildPredicates(sp.getFilters(), builder, root);
			query.where(builder.and(predicates.toArray(new Predicate[]{})));
			if(sp.getSortField()!=null){
				if(sp.isAscending()){
					query.orderBy(builder.asc(getPathFromString(sp.getSortField(), root)));
				}
				else{
					query.orderBy(builder.desc(getPathFromString(sp.getSortField(), root)));
				}
			}
        }
        
		TypedQuery<T> q = getEntityManager().createQuery(query).
				setFirstResult(sp.getFirst()).setMaxResults(sp.getPageSize());
		return q.getResultList();
	}

	

	@Override
	public Long count(SearchParameters sp) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(persistentClass);
        
        if(sp!=null){
			List<Predicate> predicates = buildPredicates(sp.getFilters(), builder, root);
			query.where(builder.and(predicates.toArray(new Predicate[]{})));
        }
        
		query.select(builder.count(root));
		return getEntityManager().createQuery(query).getSingleResult(); 
	}
	
	@Override
	public void remove(T entity) {
		getEntityManager().remove(entity);
	}
	
	@Override
	public void detach(T entity) {
		getEntityManager().detach(entity);
	}
	
	@Override
	public void flush() {
		getEntityManager().flush();
	}

	@Override
	public void remove(ID id) {
		T entity = getEntityManager().find(persistentClass, id);
		getEntityManager().remove(entity);
	}
	
	private List<Predicate> buildPredicates(Map<String, Object[]> filters, CriteriaBuilder builder, Root<T> root){
		if(filters!=null){
			List<Predicate> predicates = new ArrayList<Predicate>();
			for(Entry<String, Object[]> entry: filters.entrySet()){
				if(entry.getValue().length==1){
					predicates.add(builder.equal(getPathFromString(entry.getKey(), root), entry.getValue()[0]));
				}
				else{
					predicates.add(getPathFromString(entry.getKey(), root).in(entry.getValue()));
				}
			}
			return predicates;
		}
		return Collections.EMPTY_LIST;
	}
	
	private Path getPathFromString(String attributeName, Path<?> root){
		int index = attributeName.indexOf(".");
		if(index<0){
			return root.get(attributeName);
		}else{
			return getPathFromString(attributeName.substring(index+1),
					root.get(attributeName.substring(0,index)));
		}
	}
	
	protected EntityManager getEntityManager() {
		return manager;
	}

	protected CriteriaBuilder getCriteriaBuilder() {
		return manager.getCriteriaBuilder();
	}
}
