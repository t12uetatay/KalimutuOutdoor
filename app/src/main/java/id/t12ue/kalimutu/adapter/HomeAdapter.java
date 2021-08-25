package id.t12ue.kalimutu.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import id.t12ue.kalimutu.ui.fragment.BankFragment;
import id.t12ue.kalimutu.ui.fragment.PackagesFragment;

public class HomeAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public HomeAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PackagesFragment();
            case 1:
                return new BankFragment();
            default:
                return new  PackagesFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
