package leonardoribeiro.reservafacil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class FazerReserva extends AppCompatActivity {

    private String [] resNome = new String[]{"Outback", "AppleBee's", "Fifties", "Pizza Hut", "Chalezinho", "Fornatore"};

    private int[] resLogo = {R.drawable.spinneroutback, R.drawable.spinnerapplebee, R.drawable.spinnerfifties, R.drawable.spinnerpizzahut,
                                R.drawable.spinnerchalezinho, R.drawable.spinnerfornatore};

    private Spinner sp;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fazer_reserva);


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

    }

    public void showElemento(View view){
        String nome = (String) sp.getSelectedItem();
        long id = sp.getSelectedItemId();
        int posicao = sp.getSelectedItemPosition();

        Toast.makeText(this, "Restaurante: " + nome, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroyed();
    }
}
