package com.mo_zarara.newlanguageschat.Ui.Fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mo_zarara.newlanguageschat.Ui.Fragments.ChatsFragment;
import com.mo_zarara.newlanguageschat.Ui.Fragments.ContactsFragment;
import com.mo_zarara.newlanguageschat.Ui.Fragments.GroupsFragment;
import com.mo_zarara.newlanguageschat.Ui.Fragments.RequestsFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }





    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
        /*switch (position) {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Requests";

            default:
                return null;
        }*/
    }
}
