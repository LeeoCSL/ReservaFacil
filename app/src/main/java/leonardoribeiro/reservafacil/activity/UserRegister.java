package leonardoribeiro.reservafacil.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import leonardoribeiro.reservafacil.R;
import leonardoribeiro.reservafacil.model.Customer;
import leonardoribeiro.reservafacil.utils.DialogFactory;
import leonardoribeiro.reservafacil.utils.ToastUtils;

import static leonardoribeiro.reservafacil.utils.TextUtils.isEmailValid;

public class UserRegister extends AppCompatActivity {
    @BindView(R.id.tb)
    Toolbar mToolbar;

    @BindView(R.id.edt_nome)
    EditText mUserName;

    @BindView(R.id.edt_senha)
    EditText mUserPassword;

    @BindView(R.id.edt_tel)
    EditText mUserPhone;

    @BindView(R.id.edt_email)
    EditText mUserEmail;

    @BindView(R.id.user_image)
    CircleImageView mUserImage;


    private static final int GALLERY_PICK = 1;

    private String userName;
    private String userPassword;
    private String userPhone;
    private String userEmail;

    private DatabaseReference mDatabaseCustomers;

    private StorageReference mImageStorage;

    private Spinner sp;
    private ImageView sexo;

    private String [] resSexo = new String[]{"Masculino", "Feminino"};

    private int[] resSexoImg = {R.drawable.ic_avatar_masc, R.drawable.ic_avatar_fem};

    private FirebaseAuth mFirebaseAuth;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //cria nó no database para clientes
        mDatabaseCustomers = FirebaseDatabase.getInstance().getReference().child("customers");

        mFirebaseAuth = FirebaseAuth.getInstance();

        //seta voltar da toolbar V
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reservar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //seta voltar da toolbar ^


        //alteração da imagem e spinner para sexo V
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, resSexo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sexo = (ImageView) findViewById(R.id.user_image);

        sp = (Spinner) findViewById(R.id.spinner_sexo);
        sp.setAdapter(adapter);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sexo.setImageResource(resSexoImg[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //alteração da imagem e spinner para sexo ^

    }

    public void handlerUserRegister(View v) {
        validateDataFromEditText();
    }



    public void validateDataFromEditText() {


        userEmail = mUserEmail.getText().toString().trim();
        userName = mUserName.getText().toString().trim();
        userPhone = mUserPhone.getText().toString().trim();
        userPassword = mUserPassword.getText().toString().trim();


        if (TextUtils.isEmpty(userName)) {
            mUserName.setError(getString(R.string.name_field_empty));
            return;
        }

        if (TextUtils.isEmpty(userPhone)) {
            mUserPhone.setError(getString(R.string.invalid_telephone));
            return;
        }

        if (!isEmailValid(userEmail)) {
            mUserEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (userPassword.length() < 6) {
            mUserPassword.setError(getString(R.string.your_password_needs_more_caracters));
            return;
        }

        if (mUri == null) {
            new AlertDialog.Builder(UserRegister.this)
                    .setTitle("Faltando Foto")
                    .setMessage("Você não escolheu nenhuma foto, deseja continuar assim mesmo?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUri = null;
                            loginWithEmailAndPassword();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .setCancelable(false)
                    .show();
        } else {
            loginWithEmailAndPassword();
        }
    }

    private void loginWithEmailAndPassword() {

        DialogFactory.createDialog(this, getString(R.string.enter), getString(R.string.validating_information));

        mFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnSuccessListener(UserRegister.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userID = mFirebaseAuth.getCurrentUser().getUid();

                        if (mUri != null) {
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(mUri).build();
                            authResult.getUser().updateProfile(request);
                        }


                        Customer customer = new Customer();
                        customer.setKey(userID);
                        customer.setName(userName);
                        customer.setPhone(userPhone);
                        customer.setEmail(userEmail);

                        mDatabaseCustomers.child(userID).setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DialogFactory.destroyDialog();
                                Intent intent = new Intent(UserRegister.this, Main2ActivityUser.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(UserRegister.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        DialogFactory.destroyDialog();

                        ToastUtils.makeText(UserRegister.this, e.getMessage());

                        if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                            ToastUtils.makeText(UserRegister.this, getString(R.string.email_already_used_with_facebook_account));
                        }
                    }
                });
    }

    public void handlePhoto(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageURI = data.getData();

            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                mUri = resultUri;

                Picasso.with(getApplicationContext()).
                        load(resultUri).
                        into(mUserImage);


            }

        }
    }


}
