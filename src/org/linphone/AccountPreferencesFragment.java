package org.linphone;
/*
AccountPreferencesFragment.java
Developed pursuant to contract FCC15C0008 as open source software under GNU General Public License version 2.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.WindowManager;

import org.linphone.LinphonePreferences.AccountBuilder;
import org.linphone.core.LinphoneCoreException;
import org.linphone.ui.PreferencesListFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sylvain Berfini
 */
public class AccountPreferencesFragment extends PreferencesListFragment {
	private int n;
	private boolean isNewAccount=false;
	private LinphonePreferences mPrefs;
	private EditTextPreference mProxyPreference;
	private ListPreference mTransportPreference;
	private AccountBuilder builder;
	
	public AccountPreferencesFragment() {
		super(R.xml.account_preferences);
		mPrefs =

				LinphonePreferences.instance();
	}
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		PreferenceScreen screen = getPreferenceScreen();
		n = getArguments().getInt("Account", 0);
		if(n == mPrefs.getAccountCount()){
			isNewAccount=true;
			builder = new AccountBuilder(LinphoneManager.getLc());
			initAccountPreferencesFields(screen);
		} else {
			manageAccountPreferencesFields(screen);
		}

		// Force hide keyboard
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public static boolean isEditTextEmpty(String s){
	      return s.equals("");  // really empty.          
	}
	
	OnPreferenceChangeListener usernameChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(isEditTextEmpty(newValue.toString())) return false;
			if (isNewAccount) {
				builder.setUsername(newValue.toString());
			} else {
				mPrefs.setAccountUsername(n, newValue.toString());
			}
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener useridChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (isNewAccount) {
				builder.setUserId(newValue.toString());
			} else {
				mPrefs.setAccountUserId(n, newValue.toString());
			}		
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener passwordChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(isEditTextEmpty(newValue.toString())) return false;
			if (isNewAccount) {
				builder.setPassword(newValue.toString());
			} else {
				mPrefs.setAccountPassword(n, newValue.toString());
			}		
			return true;
		}		
	};
	OnPreferenceChangeListener domainChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(isEditTextEmpty(newValue.toString())) return false;
			if (isNewAccount) {
				builder.setDomain(newValue.toString());
			} else {
				mPrefs.setAccountDomain(n, newValue.toString());
			}			
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener displayNameChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (isNewAccount) {
				builder.setDisplayName(newValue.toString());
			} else {
				mPrefs.setAccountDisplayName(n, newValue.toString());
			}
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener transportChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String key = newValue.toString();
			if (isNewAccount) {
				//TODO
				//builder.setTransport(transport);
			} else {
				mPrefs.setAccountTransport(n, key);
				preference.setSummary(mPrefs.getAccountTransportString(n));
				preference.setDefaultValue(mPrefs.getAccountTransportKey(n));
				if (mProxyPreference != null) {
					if(mPrefs.getAccountTransportString(n).equalsIgnoreCase("tls")){
						mPrefs.setAccountProxy(n, mPrefs.getAccountProxy(n).replace("5060","5061"));

					}else if(mPrefs.getAccountTransportString(n).equalsIgnoreCase("tcp")){
						mPrefs.setAccountProxy(n, mPrefs.getAccountProxy(n).replace("5061","5060"));
					}

					String newProxy = mPrefs.getAccountProxy(n);
					mProxyPreference.setSummary(newProxy);
					mProxyPreference.setText(newProxy);
				}


			}
			return true;
		}
	};
	OnPreferenceChangeListener proxyChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String value = newValue.toString();
			if (isNewAccount) {
				builder.setProxy(newValue.toString());
				preference.setSummary(newValue.toString());
			} else {
				mPrefs.setAccountProxy(n, value);
				preference.setSummary(mPrefs.getAccountProxy(n));
				
				if (mTransportPreference != null) {
					mTransportPreference.setSummary(mPrefs.getAccountTransportString(n));
					mTransportPreference.setValue(mPrefs.getAccountTransportKey(n));
				}
			}		
			return true;
		}		
	};
	OnPreferenceChangeListener outboundProxyChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (isNewAccount) {
				builder.setOutboundProxyEnabled((Boolean) newValue);
			} else {
				mPrefs.setAccountOutboundProxyEnabled(n, (Boolean) newValue);
			}			
			return true;
		}		
	};
	OnPreferenceChangeListener expiresChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (isNewAccount) {
				builder.setExpires(newValue.toString());
			} else {
				mPrefs.setExpires(n, newValue.toString());
			}
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener prefixChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String value = newValue.toString();
			preference.setSummary(value);
			if (isNewAccount) {
				//TODO acocunt builder ste prefix
			} else {
				mPrefs.setPrefix(n, value);
			}
			return true;
		}
	};

	//Removed in VATRP-2301
//	OnPreferenceChangeListener avpfChangedListener = new OnPreferenceChangeListener() {
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			boolean value = (Boolean) newValue;
//			if (isNewAccount) {
//				builder.setAvpfEnabled(value);
//			} else {
//				mPrefs.enableAvpf(n, value);
//			}
//			return true;
//		}
//	};
//	OnPreferenceChangeListener avpfRRIntervalChangedListener = new OnPreferenceChangeListener() {
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			String value = newValue.toString();
//			try {
//				int intValue = Integer.parseInt(value);
//				if ((intValue < 1) || (intValue > 5)) {
//					return false;
//				}
//			} catch (NumberFormatException nfe) { }
//			if (isNewAccount) {
//				//TODO
//			} else {
//				mPrefs.setAvpfRRInterval(n, value);
//			}
//			preference.setSummary(value);
//			return true;
//		}
//	};
	OnPreferenceChangeListener escapeChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean value = (Boolean) newValue;
			if (isNewAccount) {
				//TODO
			} else {
				mPrefs.setReplacePlusByZeroZero(n, value);
			}
			return true;
		}
	};
	OnPreferenceChangeListener disableChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean value = (Boolean) newValue;
			if (isNewAccount) {
				builder.setEnabled(!value);
			} else {
				mPrefs.setAccountEnabled(n, !value);
			}		
			return true;
		}		
	};

	
	private void initAccountPreferencesFields(PreferenceScreen parent) {
		boolean isDefaultAccount = mPrefs.getDefaultAccountIndex() == n;
		
    	PreferenceCategory account = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_sipaccount_key));

    	EditTextPreference username = (EditTextPreference) account.getPreference(0);
    	username.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	username.setOnPreferenceChangeListener(usernameChangedListener);
		if (SettingsFragment.isAdvancedSettings) {
			account.setEnabled(true);
		}else {
			account.setEnabled(false);
		}
    	EditTextPreference userid = (EditTextPreference) account.getPreference(1);
    	userid.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	userid.setOnPreferenceChangeListener(useridChangedListener);
    	
    	EditTextPreference password = (EditTextPreference) account.getPreference(2);
        password.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	password.setOnPreferenceChangeListener(passwordChangedListener);
    	
    	EditTextPreference domain = (EditTextPreference) account.getPreference(3);
    	domain.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	domain.setOnPreferenceChangeListener(domainChangedListener);
    	
    	EditTextPreference displayName = (EditTextPreference) account.getPreference(4);
    	displayName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    	displayName.setOnPreferenceChangeListener(displayNameChangedListener);
		
    	PreferenceCategory advanced = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_advanced_key));
    	mTransportPreference = (ListPreference) advanced.getPreference(0);
    	initializeTransportPreference(mTransportPreference);
    	mTransportPreference.setOnPreferenceChangeListener(transportChangedListener);	
    	
		mProxyPreference = (EditTextPreference) advanced.getPreference(1);
		mProxyPreference.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		mProxyPreference.setOnPreferenceChangeListener(proxyChangedListener);
    	
    	CheckBoxPreference outboundProxy = (CheckBoxPreference) advanced.getPreference(2);
    	outboundProxy.setOnPreferenceChangeListener(outboundProxyChangedListener);
    	
    	EditTextPreference expires = (EditTextPreference) advanced.getPreference(3);
    	expires.setOnPreferenceChangeListener(expiresChangedListener);

    	EditTextPreference prefix = (EditTextPreference) advanced.getPreference(4);
    	prefix.setOnPreferenceChangeListener(prefixChangedListener);
//Removed in VATRP-2301
//		CheckBoxPreference avpf = (CheckBoxPreference) advanced.getPreference(5);
//		avpf.setOnPreferenceChangeListener(avpfChangedListener);
//
//		EditTextPreference avpfRRInterval = (EditTextPreference) advanced.getPreference(6);
//		avpfRRInterval.setOnPreferenceChangeListener(avpfRRIntervalChangedListener);

    	CheckBoxPreference escape = (CheckBoxPreference) advanced.getPreference(7);
		escape.setOnPreferenceChangeListener(escapeChangedListener);
    	
    	PreferenceCategory manage = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_manage_key));
    	final CheckBoxPreference disable = (CheckBoxPreference) manage.getPreference(0);
		disable.setOnPreferenceChangeListener(disableChangedListener);
    	
    	CheckBoxPreference mainAccount = (CheckBoxPreference) manage.getPreference(1);
    	mainAccount.setChecked(isDefaultAccount);
    	mainAccount.setEnabled(!mainAccount.isChecked());
    	mainAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() 
    	{
			public boolean onPreferenceClick(Preference preference) {
				mPrefs.setDefaultAccount(n);
				disable.setEnabled(false);
				disable.setChecked(false);
				preference.setEnabled(false);
				return true;
			}
		});

    	final Preference delete = manage.getPreference(2);
		delete.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        public boolean onPreferenceClick(Preference preference) {
	        	mPrefs.deleteAccount(n);
	        	LinphoneActivity.instance().displaySettings();
	        	return true;
	        }
        });
	}
	
	private void manageAccountPreferencesFields(PreferenceScreen parent) {
		boolean isDefaultAccount = mPrefs.getDefaultAccountIndex() == n;
		
    	PreferenceCategory account = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_sipaccount_key));
    	EditTextPreference username = (EditTextPreference) account.getPreference(0);
    	username.setText(mPrefs.getAccountUsername(n));
    	username.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	username.setOnPreferenceChangeListener(usernameChangedListener);
    	username.setSummary(username.getText());
		setAsAdvancedSetting(username);
    	
    	EditTextPreference userid = (EditTextPreference) account.getPreference(1);
    	userid.setText(mPrefs.getAccountUserId(n));
    	userid.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	userid.setOnPreferenceChangeListener(useridChangedListener);
    	userid.setSummary(userid.getText());
		setAsAdvancedSetting(userid);

		EditTextPreference password = (EditTextPreference) account.getPreference(2);
        password.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	password.setText(mPrefs.getAccountPassword(n));
        password.setOnPreferenceChangeListener(passwordChangedListener);
		setAsAdvancedSetting(password);
    	
    	EditTextPreference domain = (EditTextPreference) account.getPreference(3);
    	domain.setText(mPrefs.getAccountDomain(n));
    	domain.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	domain.setOnPreferenceChangeListener(domainChangedListener);
    	domain.setSummary(domain.getText());
		setAsAdvancedSetting(domain);
    	
    	EditTextPreference displayName = (EditTextPreference) account.getPreference(4);
    	displayName.setText(mPrefs.getAccountDisplayName(n));
    	displayName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    	displayName.setOnPreferenceChangeListener(displayNameChangedListener);
    	displayName.setSummary(displayName.getText());
		setAsAdvancedSetting(displayName);
		
    	PreferenceCategory advanced = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_advanced_key));
    	mTransportPreference = (ListPreference) advanced.getPreference(0);
    	initializeTransportPreference(mTransportPreference);
    	mTransportPreference.setOnPreferenceChangeListener(transportChangedListener);	
    	mTransportPreference.setSummary(mPrefs.getAccountTransportString(n));
		setAsAdvancedSetting(mTransportPreference);
    	
		mProxyPreference = (EditTextPreference) advanced.getPreference(1);
		mProxyPreference.setText(mPrefs.getAccountProxy(n));
		mProxyPreference.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		mProxyPreference.setOnPreferenceChangeListener(proxyChangedListener);
		mProxyPreference.setSummary("".equals(mProxyPreference.getText()) || (mProxyPreference.getText() == null) ? getString(R.string.pref_help_proxy) : mProxyPreference.getText());
		setAsAdvancedSetting(mProxyPreference);
    	
    	CheckBoxPreference outboundProxy = (CheckBoxPreference) advanced.getPreference(2);
    	outboundProxy.setChecked(mPrefs.isAccountOutboundProxySet(n));
    	outboundProxy.setOnPreferenceChangeListener(outboundProxyChangedListener);
		setAsAdvancedSetting(outboundProxy);
    	
    	EditTextPreference expires = (EditTextPreference) advanced.getPreference(3);
    	expires.setText(mPrefs.getExpires(n));
    	expires.setOnPreferenceChangeListener(expiresChangedListener);
    	expires.setSummary(mPrefs.getExpires(n));
		setAsAdvancedSetting(expires);

    	EditTextPreference prefix = (EditTextPreference) advanced.getPreference(4);
    	String prefixValue = mPrefs.getPrefix(n);
    	prefix.setSummary(prefixValue);
    	prefix.setText(prefixValue);
    	prefix.setOnPreferenceChangeListener(prefixChangedListener);
		setAsAdvancedSetting(prefix);


//Removed in VATRP-2301
//		CheckBoxPreference avpf = (CheckBoxPreference) advanced.getPreference(5);
//		avpf.setChecked(mPrefs.avpfEnabled(n));
//		avpf.setOnPreferenceChangeListener(avpfChangedListener);
//
//		EditTextPreference avpfRRInterval = (EditTextPreference) advanced.getPreference(6);
//		avpfRRInterval.setText(mPrefs.getAvpfRRInterval(n));
//		avpfRRInterval.setOnPreferenceChangeListener(avpfRRIntervalChangedListener);
//		avpfRRInterval.setSummary(mPrefs.getAvpfRRInterval(n));

    	CheckBoxPreference escape = (CheckBoxPreference) advanced.getPreference(5);
		escape.setChecked(mPrefs.getReplacePlusByZeroZero(n));
		escape.setOnPreferenceChangeListener(escapeChangedListener);
		setAsAdvancedSetting(escape);
    	
    	PreferenceCategory manage = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_manage_key));
    	final CheckBoxPreference disable = (CheckBoxPreference) manage.getPreference(0);
    	disable.setEnabled(true);
    	disable.setChecked(!mPrefs.isAccountEnabled(n));
    	disable.setOnPreferenceChangeListener(disableChangedListener);
    	
    	CheckBoxPreference mainAccount = (CheckBoxPreference) manage.getPreference(1);
    	mainAccount.setChecked(isDefaultAccount);
    	mainAccount.setEnabled(!mainAccount.isChecked());
    	mainAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() 
    	{
			public boolean onPreferenceClick(Preference preference) {
				mPrefs.setDefaultAccount(n);
				disable.setEnabled(false);
				disable.setChecked(false);
				preference.setEnabled(false);
				return true;
			}
		});

    	final Preference delete = manage.getPreference(2);
    	delete.setEnabled(true);
    	delete.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        public boolean onPreferenceClick(Preference preference) {
	        	mPrefs.deleteAccount(n);
	        	LinphoneActivity.instance().displaySettings();
	        	return true;
	        }
        });
	}
	
	private void initializeTransportPreference(ListPreference pref) {
		List<CharSequence> entries = new ArrayList<CharSequence>();
		List<CharSequence> values = new ArrayList<CharSequence>();
//		removed udp option to disable users from being encouraged to select it.
//		entries.add(getString(R.string.pref_transport_udp));
//		values.add(getString(R.string.pref_transport_udp_key));
		entries.add(getString(R.string.pref_transport_tcp));
		values.add(getString(R.string.pref_transport_tcp_key));
		
		if (!getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
			entries.add(getString(R.string.pref_transport_tls));
			values.add(getString(R.string.pref_transport_tls_key));
		}
		setListPreferenceValues(pref, entries, values);
		
		if (! isNewAccount) {
			pref.setSummary(mPrefs.getAccountTransportString(n));
			pref.setDefaultValue(mPrefs.getAccountTransportKey(n));
			pref.setValueIndex(entries.indexOf(mPrefs.getAccountTransportString(n)));
		} else {
//changed to tcp even in new accounts option to disable users from being encouraged to select it.
			pref.setSummary(getString(R.string.pref_transport_tcp));
			pref.setDefaultValue(getString(R.string.pref_transport_tcp));
			pref.setValueIndex(entries.indexOf(getString(R.string.pref_transport_tcp)));
		}
	}
	
	private static void setListPreferenceValues(ListPreference pref, List<CharSequence> entries, List<CharSequence> values) {
		CharSequence[] contents = new CharSequence[entries.size()];
		entries.toArray(contents);
		pref.setEntries(contents);
		contents = new CharSequence[values.size()];
		values.toArray(contents);
		pref.setEntryValues(contents);
	}
	
	@Override
	public void onPause() {
		super.onPause();		
		if (LinphoneActivity.isInstanciated()) {
			try {
				if(isNewAccount){
					builder.saveNewAccount();
				}
			} catch (LinphoneCoreException e) {
				e.printStackTrace();
			}
			LinphoneActivity.instance().isNewProxyConfig();
			LinphoneManager.getLc().refreshRegisters();
		}
	}

	public void setAsAdvancedSetting(Preference pref){
		if (SettingsFragment.isAdvancedSettings) {
			pref.setEnabled(true);
		}else {
			pref.setEnabled(false);
		}
	}
}
