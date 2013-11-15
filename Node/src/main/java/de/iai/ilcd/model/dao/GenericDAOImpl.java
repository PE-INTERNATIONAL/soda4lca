package de.iai.ilcd.model.dao;

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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iai.ilcd.persistence.PersistenceUtil;
import eu.europa.ec.jrc.lca.commons.dao.SearchParameters;

public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

	private static final Logger LOGGER = LoggerFactory.getLogger( GenericDAOImpl.class );

	private Class<T> persistentClass;

	@SuppressWarnings( "unchecked" )
	public GenericDAOImpl() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected Class<T> getPersistentClass() {
		return persistentClass;
	}

	public T findById( ID id ) {
		try {
			return PersistenceUtil.getEntityManager().find( persistentClass, id );
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public List<T> findAll() {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = manager.getCriteriaBuilder();
			CriteriaQuery<T> query = builder.createQuery( persistentClass );
			TypedQuery<T> q = manager.createQuery( query );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public T saveOrUpdate( T entity ) {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			if ( getIdValue( entity ) == null ) {
				manager.getTransaction().begin();
				manager.persist( entity );
				manager.getTransaction().commit();
				return entity;
			}
			else {
				manager.getTransaction().begin();
				entity = manager.merge( entity );
				manager.flush();
				manager.getTransaction().commit();
				return entity;
			}
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	protected String getIdPropertyName() {
		return getIdPropertyName( getPersistentClass() );
	}

	private String getIdPropertyName( Class<?> clazz ) {
		for ( Field f : clazz.getDeclaredFields() ) {
			if ( f.getAnnotation( Id.class ) != null ) {
				return f.getName();
			}
		}
		if ( clazz.getSuperclass() != null ) {
			return getIdPropertyName( clazz.getSuperclass() );
		}
		return null;
	}

	private ID getIdValue( T entity ) {
		Field[] fields = persistentClass.getDeclaredFields();
		for ( Field currentField : fields ) {
			if ( currentField.isAnnotationPresent( javax.persistence.Id.class ) )
				try {
					currentField.setAccessible( true );
					ID id = (ID) currentField.get( entity );
					currentField.setAccessible( false );
					return id;
				}
				catch ( IllegalArgumentException e ) {
					LOGGER.error( "[getIdValue]", e );
				}
				catch ( IllegalAccessException e ) {
					LOGGER.error( "[getIdValue]", e );
				}
		}
		return null;
	}

	public void remove( T entity ) {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			manager.getTransaction().begin();
			manager.remove( entity );
			manager.getTransaction().commit();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public void remove( ID id ) {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			manager.getTransaction().begin();
			T entity = manager.find( persistentClass, id );
			manager.remove( entity );
			manager.getTransaction().commit();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public List<T> find( SearchParameters sp ) {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = manager.getCriteriaBuilder();
			CriteriaQuery<T> query = builder.createQuery( persistentClass );

			if ( sp != null ) {
				Root<T> root = query.from( persistentClass );
				List<Predicate> predicates = buildPredicates( sp.getFilters(), builder, root );
				query.where( builder.and( predicates.toArray( new Predicate[] {} ) ) );
			}

			TypedQuery<T> q = manager.createQuery( query ).setFirstResult( sp.getFirst() ).setMaxResults( sp.getPageSize() );
			return q.getResultList();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	public Long count( SearchParameters sp ) {
		EntityManager manager = PersistenceUtil.getEntityManager();
		try {
			CriteriaBuilder builder = manager.getCriteriaBuilder();
			CriteriaQuery<Long> query = builder.createQuery( Long.class );
			Root<T> root = query.from( persistentClass );

			if ( sp != null ) {
				List<Predicate> predicates = buildPredicates( sp.getFilters(), builder, root );
				query.where( builder.and( predicates.toArray( new Predicate[] {} ) ) );
			}

			query.select( builder.count( root ) );
			return manager.createQuery( query ).getSingleResult();
		}
		finally {
			PersistenceUtil.closeEntityManager();
		}
	}

	private List<Predicate> buildPredicates( Map<String, Object[]> filters, CriteriaBuilder builder, Root<T> root ) {
		if ( filters != null ) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for ( Entry<String, Object[]> entry : filters.entrySet() ) {
				if ( entry.getValue().length == 1 ) {
					predicates.add( builder.equal( root.get( entry.getKey() ), entry.getValue()[0] ) );
				}
				else {
					predicates.add( root.get( entry.getKey() ).in( entry.getValue() ) );
				}
			}
			return predicates;
		}
		return Collections.EMPTY_LIST;
	}
}
