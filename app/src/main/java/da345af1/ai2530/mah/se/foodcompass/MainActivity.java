package da345af1.ai2530.mah.se.foodcompass;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    FragmentController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fm = getSupportFragmentManager();
        if(controller == null) {
            controller = new FragmentController(this);
        }
    }

    public void setFragment(Fragment fragment) {

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

    }

    public Fragment getFragment(String tag) {

        return fm.findFragmentByTag(tag);
    }
}
