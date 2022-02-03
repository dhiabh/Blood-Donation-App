package ma.ensaf.blooddonationapp.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ma.ensaf.blooddonationapp.fragments.thirdPartyLogin.SignUpDonorFragTP;
import ma.ensaf.blooddonationapp.fragments.thirdPartyLogin.SignUpRecipientFragTP;

public class SignUpTPAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public SignUpTPAdapter(FragmentManager fm, Context context, int totalTabs)
    {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SignUpDonorFragTP signUpDonorFragTP = new SignUpDonorFragTP();
                return signUpDonorFragTP;
            case 1:
                SignUpRecipientFragTP signUpRecipientFragTP = new SignUpRecipientFragTP();
                return signUpRecipientFragTP;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
