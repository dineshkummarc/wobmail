package net.xytra.wobmail.components;

// Generated by the WOLips Templateengine Plug-in at Apr 18, 2007 9:01:26 PM

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import net.xytra.wobmail.application.Application;
import net.xytra.wobmail.application.Session;
import net.xytra.wobmail.mailconn.WobmailException;
import net.xytra.wobmail.mailconn.manager.WobmailSessionManager;
import net.xytra.wobmail.util.LocaleUtils;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXNonSynchronizingComponent;
import er.extensions.foundation.ERXProperties;
import er.extensions.localization.ERXLocalizer;
import er.extensions.logging.ERXLogger;

public class Main extends ERXNonSynchronizingComponent
{
	public static final String INVALID_USER_PASS_ERROR_KEY = "Main.InvalidUsernameOrPassword";

	public String selectedLanguage = LocaleUtils.defaultERXLocalizerLocaleName();

	public String username;
	public String password;

	public String errorMessage;

	public String currentLanguage;

	private NSArray<String> availableLanguages; 
	private Map<String, String> languageNameMap = new HashMap<String, String>();
	private ERXLocalizer localizer;

	public Main(WOContext context) {
		super(context);
	}

	public WOComponent loginAction()
	{
		errorMessage = null;

		// Don't try to login with no username nor password but don't display an
		// error message either:
		if ((username == null) && (password == null)) {
			return (this);
		}

		WobmailSessionManager manager = ((Application)Application.application()).getDefaultSessionManager();

		try {
			manager.registerMailSession((Session)session(), username, password);
		}
		catch (AuthenticationFailedException e) {
			// Failed authentication suggests we might want to null out password first:
			password = null;

			// Display error message:
			errorMessage = getLocalizer().localizedStringForKeyWithDefault(INVALID_USER_PASS_ERROR_KEY);
			ERXLogger.log.debug(e);
		} catch (MessagingException me) {
			throw (new WobmailException(me));
		}

		if (errorMessage != null) {
			return (this);
		}

		((Session)session()).setUsername(username);
		((Session)session()).setLanguage(selectedLanguage);

		return (pageWithName(XWMList.class.getName()));
	}

	// Data
	public NSArray<String> getAvailableLanguages() {
		if (availableLanguages == null) {
			availableLanguages = ERXProperties.arrayForKeyWithDefault("er.extensions.ERXLocalizer.availableLanguages", new NSArray<String>("en_CA"));
		}

		return (availableLanguages);
	}

	public String getCurrentLanguageName() {
		if (languageNameMap.get(currentLanguage) == null) {
			Locale locale = LocaleUtils.localeForLocaleName(currentLanguage);
			String rawLanguageName = locale.getDisplayLanguage(locale);

			// Capitalize first letter of language name:
			String firstLetter = rawLanguageName.substring(0,1);  // Get first letter
	        String remainder   = rawLanguageName.substring(1);    // Get remainder of word.
	        String presentableLanguageName = firstLetter.toUpperCase(locale) + remainder;

			languageNameMap.put(currentLanguage, presentableLanguageName);
		}

		return (languageNameMap.get(currentLanguage));
	}

	public String getLanguageOptionOtherTag() {
		return (currentLanguage.equals(selectedLanguage) ? "selected" : "");
	}

	public String getLocalizedStringChangeLanguage() {
		return (getLocalizer().localizedStringForKeyWithDefault("Main.ChangeLanguage"));
	}

	public String getLocalizedStringLogin() {
		return (getLocalizer().localizedStringForKeyWithDefault("Main.Login"));
	}

	public ERXLocalizer getLocalizer() {
		if (localizer == null) {
			localizer = ERXLocalizer.localizerForLanguage(selectedLanguage);
		}

		return (localizer);
	}

}
