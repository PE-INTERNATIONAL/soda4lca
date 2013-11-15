package eu.europa.ec.jrc.lca.registry.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ILCDMessageSource {

	@Autowired 
	private MessageSource messageSource;
	
	public String getTranslation(String code) {
		return messageSource.getMessage(code, null, code,
				LocaleContextHolder.getLocale());
	}
	
	public String getTranslation(String code, Object[] params) {
		return messageSource.getMessage(code, params, code,
				LocaleContextHolder.getLocale());
	}
}
