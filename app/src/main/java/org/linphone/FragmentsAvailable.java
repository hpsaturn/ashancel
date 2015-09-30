package org.linphone;
/*
FragmentsAvailable.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

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
/**
 * @author Sylvain Berfini
 */
public enum FragmentsAvailable {
	UNKNOW,
	LOGIN,
	REGISTRATION,
	WELCOME,
	START,
	HISTORY,
	HISTORY_DETAIL,
	CONTACTS,
	CONTACT,
	EDIT_CONTACT,
	ABOUT,
	ACCOUNT_SETTINGS,
	SETTINGS,
	CHATLIST,
	CHAT,
	RINGS,
	RING,
	EDIT_RING,
	PANIC,
	TRAKING;

	public boolean shouldAddToBackStack() {
		return true;
	}

	public boolean shouldAnimate() {
		return true;
	}

	public boolean isRightOf(FragmentsAvailable fragment) {
		switch (this) {
			case START:
				return START.isRightOf(fragment) || fragment == START;

			case HISTORY:
				return fragment == UNKNOW;

			case HISTORY_DETAIL:
				return HISTORY.isRightOf(fragment) || fragment == HISTORY;

			case CONTACTS:
				return HISTORY_DETAIL.isRightOf(fragment) || fragment == HISTORY_DETAIL;

			case CONTACT:
				return CONTACTS.isRightOf(fragment) || fragment == CONTACTS;

			case EDIT_CONTACT:
				return CONTACT.isRightOf(fragment) || fragment == CONTACT;

			case CHAT:
				return CHATLIST.isRightOf(fragment) || fragment == CHATLIST;

			case REGISTRATION:
				return REGISTRATION.isRightOf(fragment) || fragment == REGISTRATION;

			case SETTINGS:
				return CHATLIST.isRightOf(fragment) || fragment == CHATLIST; // || fragment == FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT;

			case ABOUT:
			case ACCOUNT_SETTINGS:
				return SETTINGS.isRightOf(fragment) || fragment == SETTINGS;

			default:
				return false;
		}
	}

	public boolean shouldAddItselfToTheRightOf(FragmentsAvailable fragment) {
		switch (this) {
			case HISTORY_DETAIL:
				return fragment == HISTORY;

			case CONTACT:
				return fragment == CONTACTS;

			case EDIT_CONTACT:
				return fragment == CONTACT || fragment == CONTACTS;

			case CHAT:
				return fragment == CHATLIST;

			default:
				return false;
		}
	}
}
