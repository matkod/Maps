package com.example.mateus.maps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class EditLocationActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Lugar lugar;
    private ArrayList<Lugar> lugares;

    private int position;

    private SeekBar radiusSeekbar;
    private TextView result;

    private DatabaseLugar db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        db = new DatabaseLugar(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button bt = (Button) findViewById(R.id.btSaveLocation);
        bt.setOnClickListener(this);

        radiusSeekbar = (SeekBar) findViewById(R.id.radiusSeekBar);
        radiusSeekbar.setOnSeekBarChangeListener(this);

        result = (TextView) findViewById(R.id.textViewRadius);

        if (savedInstanceState != null) {
            Log.d("EditLocationActivity", "Carregando de savedInstanceState");
            final ArrayList<Lugar> tmp = savedInstanceState.getParcelableArrayList(MainActivity.LIST_LOCATION);
            if (tmp != null)
                LocationManager.getInstance().setLugares(tmp);
        }

        lugares = LocationManager.getInstance().getLugares();

        if (lugares.isEmpty()) {
            ArrayList<Lugar> tmp = db.buscar();

            for (int i = 0; i < tmp.size(); ++i) {
                lugares.add(tmp.get(i));
            }
        }

        LatLng latlng = getIntent().getParcelableExtra(MainActivity.CREATE_LOCATION);
        if (latlng != null) {
            position = lugares.size() - 1;

            lugar = new Lugar("", latlng.latitude, latlng.longitude);

            lugares.add(lugar);
            db.inserir(lugar);

            Log.d("latlng", getIntent().getParcelableExtra(MainActivity.CREATE_LOCATION).toString());
        } else {
            position = getIntent().getIntExtra(MainActivity.EDIT_LOCATION, -1);

            lugar = lugares.get(position);

            radiusSeekbar.setProgress(lugar.getRaio());
            ((TextView) findViewById(R.id.editTextName)).setText(lugar.getNome());

            Switch active = (Switch) findViewById(R.id.switchActive);
            active.setChecked(lugar.isActive());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_remove) {
            remove(true);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("EditLocationActivity", "onSaveInstanceState");
        outState.putParcelableArrayList(MainActivity.LIST_LOCATION, lugares);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btSaveLocation) {
            save(true);

            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        result.setText(String.format(getResources().getString(R.string.radius), progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("back", "back");
    }

    private void save(boolean showToast) {
        TextView nameView = (TextView) findViewById(R.id.editTextName);
        Switch s1 = (Switch) findViewById(R.id.switchActive);

        lugar.setNome(nameView.getText().toString());
        lugar.setRaio(radiusSeekbar.getProgress());
        lugar.setIsActive(s1.isChecked());

        db.atualizar(lugar);

        if (showToast) {
            Toast.makeText(this, lugar.getNome() + " saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void remove(boolean showToast) {
        db.excluir(lugar);

        lugares.remove(position);

        if (showToast) {
            Toast.makeText(this, lugar.getNome() + " removed", Toast.LENGTH_SHORT).show();
        }
    }
}
