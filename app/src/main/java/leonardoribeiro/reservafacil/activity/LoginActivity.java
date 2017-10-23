package leonardoribeiro.reservafacil.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import leonardoribeiro.reservafacil.R;
import leonardoribeiro.reservafacil.utils.TextUtils;
import leonardoribeiro.reservafacil.utils.ToastUtils;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();
    @BindView(R.id.input_email)
    EditText mLoginEmail;

    @BindView(R.id.input_password)
    EditText mLoginPassword;


    private String email;
    private String password;


    private DatabaseReference mOwnerReference;

    private CallbackManager mCallbackManager;

    private DatabaseReference mDatabaseCustomers;

    private ProgressDialog mDialog;

    EditText input_email;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAnalytics mFA;

    Date dt1, dt2;
    long dtf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFA = FirebaseAnalytics.getInstance(this);

        mDatabaseCustomers = FirebaseDatabase.getInstance().getReference().child("customers");
        mOwnerReference = FirebaseDatabase.getInstance().getReference().child("restaurant");

        mFirebaseAuth = FirebaseAuth.getInstance();

        input_email = (EditText) findViewById(R.id.input_email);
        input_email.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inicioTempo();

                } else {
                    fimTempo();
                }


            }

        });
    }

    public void handlerUserLogin(View view) {
        validateEditText();
    }

    private void validateEditText() {

        email = mLoginEmail.getText().toString().trim();
        password = mLoginPassword.getText().toString().trim();

        if (!TextUtils.isEmailValid(email)) {
            mLoginEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (android.text.TextUtils.isEmpty(password)) {
            mLoginPassword.setError("o campo senha esta vazio");
            return;
        }
        loginWithEmailAndPassword();
    }

    private void loginWithEmailAndPassword() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Aguarde");
        dialog.setMessage(getString(R.string.validating_information));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final String userID = authResult.getUser().getUid();
                        mOwnerReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(userID)) {

                                    DatabaseReference requestRestaurantRef = FirebaseDatabase
                                            .getInstance().getReference().child("restaurant");

                                    requestRestaurantRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(userID)) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, Main2ActivityRestaurant.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                dialog.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                builder.setTitle("ops!");
                                                builder.setMessage("nao encontramos seu login");
                                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            dialog.dismiss();
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, Main2ActivityUser.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                            ToastUtils.makeText(LoginActivity.this,
                                    getString(R.string.email_already_used_with_facebook_account));
                            return;
                        }
                        if (e.getClass() == FirebaseAuthInvalidUserException.class) {
                            ToastUtils.makeText(LoginActivity.this,"Email não cadastrado.");
                            return;
                        }
                        if (e.getClass() == FirebaseAuthInvalidCredentialsException.class) {
                            mLoginPassword.setError("Senha invalida");
                            return;
                        } else {
                            ToastUtils.makeText(LoginActivity.this,"Algo deu errado, tente novamente.");
                        }
                        Log.d(TAG, e.getMessage() + " Exception");
                    }
                });

    }


    public void registrar(View v) {
        startActivity(new Intent(this, RegisterChoice.class));
    }




    public void inicioTempo() {
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("mm:ss");
        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        dt1 = data_atual;
        String hora_atual = dateFormat_hora.format(data_atual);


        Log.i("data_atual", data_atual.toString());
        Log.i("hora_atual", hora_atual); // Esse é o que você quer
        Toast.makeText(getApplicationContext(), hora_atual, Toast.LENGTH_SHORT).show();
    }

    public void fimTempo() {
        SimpleDateFormat dateFormat_hora2 = new SimpleDateFormat("mm:ss");
        Date data2 = new Date();

        Calendar cal2 = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        cal2.setTime(data2);
        Date data_atual2 = cal2.getTime();


        dt2 = data_atual2;

        calendar.setTimeInMillis(dt2.getTime() - dt1.getTime());

        dtf = calendar.getTimeInMillis();

        long dtfs = (dtf / 1000) % 60;

        String hora_atual2 = dateFormat_hora2.format(data_atual2);


        Log.i("data_atual", data_atual2.toString());
        Log.i("hora_atual", hora_atual2); // Esse é o que você quer
        Log.i("conta", String.valueOf(dtfs)); // Esse é o que você quer
        Toast.makeText(getApplicationContext(), hora_atual2 + " ///// " + dtfs, Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putLong("tempo_digitacao", dtfs);
        mFA.logEvent("tempo_digitacao", bundle);

    }


}
