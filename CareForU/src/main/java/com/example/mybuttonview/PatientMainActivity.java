package com.example.mybuttonview;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class PatientMainActivity extends AppCompatActivity implements
        DateDialogFragment.NoticeDialogListener{
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView navigation;
    //声明Sharedpreferenced对象
    private SharedPreferences patientSP ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_view_guide);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //设置BottomNavigationView底部导航
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //为ViewPager配置一个PagerAdapter
        MyFragmentPagerAdapter mAdapter= new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
        viewPager.setAdapter(mAdapter);
    }

    //设置OnNavigationItemSelectedListener监听事件，不同item激活时显示相应的fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        //设置导航切换时显示相应的viewpager
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.patient_choice:
                    PatientMainActivity.this.setTitle("用药选择");
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.patient_notice:
                    PatientMainActivity.this.setTitle("服药提醒");
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.patient_info:
                    PatientMainActivity.this.setTitle("我的");
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    //将fragment加入PagerAdapter
    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PatientFragmentSortSickness();
                case 1:
                    return new PatientFragmentPillNotice();
                case 2:
                    return new PatientFragmentMyInfo();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    //设置OnPageChangeListener监听事件，滑动时激活底部相应的item
    private ViewPager.OnPageChangeListener mOnPageChangeListener=new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            if (menuItem != null) {
                menuItem.setChecked(false);
            } else {
                navigation.getMenu().getItem(0).setChecked(false);
            }
            menuItem = navigation.getMenu().getItem(position);
            menuItem.setChecked(true);
            if(position == 0){
                PatientMainActivity.this.setTitle("用药选择");
            }else if(position == 1){
                PatientMainActivity.this.setTitle("服药提醒");
            }else if(position == 2){
                PatientMainActivity.this.setTitle("我的");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    //用于实现PatientFragmentPillNotice调用的datedialog接口
    @Override
    public void onDateDialogDateSet(DialogFragment dialog) {
        Button getDate=(Button)findViewById(R.id.getDate);
        if(getDate!=null) {
            DateDialogFragment dialogFragment = (DateDialogFragment) dialog;
            getDate.setText(dialogFragment.getDateString());
            patientSP=getSharedPreferences("Patient",0);
            SharedPreferences.Editor editor=patientSP.edit();
            editor.putString("dayofweek",dialogFragment.getDayofWeek());
            editor.apply();
        }
    }

}
