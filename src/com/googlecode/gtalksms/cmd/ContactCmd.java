package com.googlecode.gtalksms.cmd;

import java.util.ArrayList;

import com.googlecode.gtalksms.MainService;
import com.googlecode.gtalksms.R;
import com.googlecode.gtalksms.data.contacts.Contact;
import com.googlecode.gtalksms.data.contacts.ContactAddress;
import com.googlecode.gtalksms.data.contacts.ContactsManager;
import com.googlecode.gtalksms.data.phone.Phone;
import com.googlecode.gtalksms.xmpp.XmppMsg;

public class ContactCmd extends CommandHandlerBase {
    public ContactCmd(MainService mainService) {
        super(mainService, new String[] {"contact"}, CommandHandlerBase.TYPE_CONTACTS);
    }
   
    @Override
    protected void execute(String cmd, String searchedText) {
    
        ArrayList<Contact> contacts = ContactsManager.getMatchingContacts(sContext, searchedText);

        if (contacts.size() > 0) {

            if (contacts.size() > 1) {
                send(R.string.chat_contact_found, contacts.size(), searchedText);
            }

            for (Contact contact : contacts) {
                XmppMsg strContact = new XmppMsg();
                strContact.appendBoldLine(contact.name);

                // strContact.append(Tools.LineSep + "Id : " + contact.id);
                // strContact.append(Tools.LineSep + "Raw Ids : " + TextUtils.join(" ",
                // contact.rawIds));

                ArrayList<Phone> mobilePhones = ContactsManager.getPhones(sContext, contact.id);
                if (mobilePhones.size() > 0) {
                    strContact.appendItalicLine(getString(R.string.chat_phones));
                    for (Phone phone : mobilePhones) {
                        strContact.append(phone.getLabel() + " - " + phone.getCleanNumber());
                        // append an asterix to mark the default number
                        if (phone.isDefaultNumber()) {
                            strContact.appendBold(" *");
                        }
                        strContact.newLine();
                    }
                }

                ArrayList<ContactAddress> emails = ContactsManager.getEmailAddresses(sContext, contact.id);
                if (emails.size() > 0) {
                    strContact.appendItalicLine(getString(R.string.chat_emails));
                    for (ContactAddress email : emails) {
                        strContact.appendLine(email.label + " - " + email.address);
                    }
                }

                ArrayList<ContactAddress> addresses = ContactsManager.getPostalAddresses(sContext, contact.id);
                if (addresses.size() > 0) {
                    strContact.appendItalicLine(getString(R.string.chat_addresses));
                    for (ContactAddress address : addresses) {
                        strContact.appendLine(address.label + " - " + address.address);
                    }
                }
                send(strContact);
            }
        } else {
            send(R.string.chat_no_match_for, searchedText);
        }
    }
    
    @Override
    public String[] help() {
        String[] s = { 
                getString(R.string.chat_help_contact, makeBold("\"contact:#contact#\""))
                };
        return s;
    }
}
