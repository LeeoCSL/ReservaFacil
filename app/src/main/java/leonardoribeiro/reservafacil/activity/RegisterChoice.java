package leonardoribeiro.reservafacil.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import leonardoribeiro.reservafacil.R;

public class RegisterChoice extends AppCompatActivity {
    @BindView(R.id.tb)
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_choice);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reservar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void RegisterUser(View v){

            startActivity(new Intent(this, UserRegister.class));
    }
    public void RegisterRestaurant(View v){
        startActivity(new Intent(this, RestaurantRegister.class));
    }

}
