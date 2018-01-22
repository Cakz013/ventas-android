package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cesar.empresa.R;

import java.io.File;

/**
 * Created by Cesar on 7/17/2017.
 */

public class FormInstalarNuevaVersion extends Activity {


    public String apkFileName;

    public void accionInstalarNuevaVersionApp(View view) {
        //new DownloadFile(this, this).execute(Config.APK_INSTALADOR_TOMA_PEDIDOS_URL_DOWNLOAD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_instalar_nueva_version);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_instalar_nueva_version, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.d("Instalar APK: " + apkFileName);
        File file = new File(apkFileName );
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent2);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_form_instalar_nueva_version, container,
                    false);
            return rootView;
        }
    }

}

