package ma.ensaf.blooddonationapp.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ma.ensaf.blooddonationapp.fragments.LoginTabFragment;
import ma.ensaf.blooddonationapp.fragments.SignUpDonorTabFragment;
import ma.ensaf.blooddonationapp.fragments.SignUpRecipientTabFragment;

public class LoginAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs)
    {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignUpRecipientTabFragment signUpRecipientTabFragment = new SignUpRecipientTabFragment();
                return signUpRecipientTabFragment;
            case 2:
                SignUpDonorTabFragment signUpDonorTabFragment = new SignUpDonorTabFragment();
                return signUpDonorTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
