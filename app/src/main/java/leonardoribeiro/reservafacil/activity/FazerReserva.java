package leonardoribeiro.reservafacil.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import leonardoribeiro.reservafacil.R;

public class FazerReserva extends AppCompatActivity {

    private String [] resNome = new String[]{"Outback", "AppleBee's", "Fifties", "Pizza Hut", "Chalezinho", "Fornatore"};

    private int[] resLogo = {R.drawable.spinneroutback, R.drawable.spinnerapplebee, R.drawable.spinnerfifties, R.drawable.spinnerpizzahut,
                                R.drawable.spinnerchalezinho, R.drawable.spinnerfornatore};

    private Spinner sp;

    private ImageView iv;

    @BindView(R.id.tb)
    Toolbar mToolbar;

   ScrollableNumberPicker NP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fazer_reserva);


        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reservar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, resNome);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        iv = (ImageView) findViewById(R.id.ivRestaurante);

        sp = (Spinner) findViewById(R.id.spRestaurante);
        sp.setAdapter(adapter);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            iv.setImageResource(resLogo[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    NP = (ScrollableNumberPicker) findViewById(R.id.NP);

    }

    public void reservar(View view){
        String nome = (String) sp.getSelectedItem();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nome", nome);
        editor.commit();
        Toast.makeText(this, "Seus dados foram enviados ao restaurante: " + nome+ "\nVerifique sua reserva em 'verificar reservas'", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Sua reserva foi feita para "+ String.format(NP.getValue()+ " pessoa(s)"), Toast.LENGTH_SHORT).show();



    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroyed();
    }


}
