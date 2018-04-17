package nikodem_gortych.fftapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Fragment_FFT.OnFragmentInteractionListener,
        Fragment_Accelerometer.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    private ProgressDialog dialog;

    public static int sensorSpeed_index = 1;
    private static int[] sensorSpeed = {SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST};

    private float gravity[] = {0, 0, (float) 9.81};
    private float alpha = (float) 0.8;
    private int flag = 0;

    public Fragment_Accelerometer fragment_acc;
    public Fragment_FFT fragment_fft;
    public IFFT_Activity ifft_activity;

    private ViewPager mViewPager;

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            fragment_acc.readFromAccelerometer(sensorEvent.values[0] - gravity[0], sensorEvent.values[1] - gravity[1], sensorEvent.values[2] - gravity[2]);
            fragment_fft.readFromAccelerometer(sensorEvent.values[0] - gravity[0], sensorEvent.values[1] - gravity[1], sensorEvent.values[2] - gravity[2]);
            ifft_activity.updateData(sensorEvent.values[0] - gravity[0], sensorEvent.values[1] - gravity[1], sensorEvent.values[2] - gravity[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // RUN SUPER | REGISTER ACTIVITY AS NULL IN APP CLASS
        super.onDestroy();
        onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment_fft = new Fragment_FFT();
        fragment_acc = new Fragment_Accelerometer();
        ifft_activity = new IFFT_Activity();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if ((mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)) == null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (mAccelerometer != null) {
            ((ImageButton) findViewById(R.id.btnStart)).setEnabled(true);
        } else {
            Toast.makeText(this, "No accelerometer available.", Toast.LENGTH_LONG).show();
        }
    }

    public static void setSensorSpeed(int i) {
        sensorSpeed_index = i;
    }


    public void startRecording(View v) {
        flag = 1;
        fragment_acc.clearSeries();
        fragment_fft.clearData();
        ifft_activity.clearData();
        mSensorManager.registerListener(mListener, mAccelerometer, sensorSpeed[sensorSpeed_index]);

        ((Button) findViewById(R.id.btnIFFT)).setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.btnIFFT)).setEnabled(false);
        ((ImageButton) findViewById(R.id.btnStart)).setEnabled(false);
        ((ImageButton) findViewById(R.id.btnStop)).setEnabled(true);
    }

    public void stopRecording(View v) {
        mSensorManager.unregisterListener(mListener);

        if (flag == 1) {
            ((Button) findViewById(R.id.btnIFFT)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.btnIFFT)).setEnabled(true);
        }
        flag = 0;
        ((ImageButton) findViewById(R.id.btnStart)).setEnabled(true);
        ((ImageButton) findViewById(R.id.btnStop)).setEnabled(false);
    }

    public void goToIFFTActivity(View v) {

        Intent nowyEkran = new Intent(getApplicationContext(), IFFT_Activity.class);
        startActivity(nowyEkran);

    }

    @Override
    protected void onPause() {
        ((ImageButton) findViewById(R.id.btnStop)).performClick();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intentAbout = new Intent(this, OptionsActivity.class);
            startActivity(intentAbout);
            onPause();
            return true;
        }
        if (id == R.id.about_activity) {
            Intent intentAbout = new Intent(this, AboutAppActivity.class);
            startActivity(intentAbout);
            onPause();
            return true;
        }
        if (id == R.id.about_FFT) {
            Intent intentAbout = new Intent(this, AboutFFTActivity.class);
            startActivity(intentAbout);
            onPause();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //   TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return fragment_acc;
                case 1:
                    return fragment_fft;

            }
            return null;

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.data);
                case 1:
                    return getResources().getString(R.string.fft);
            }
            return null;
        }
    }
}


