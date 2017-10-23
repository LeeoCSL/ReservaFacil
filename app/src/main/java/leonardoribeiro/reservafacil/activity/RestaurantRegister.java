package leonardoribeiro.reservafacil.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import leonardoribeiro.reservafacil.R;
import leonardoribeiro.reservafacil.model.Restaurant;
import leonardoribeiro.reservafacil.utils.DialogFactory;
import leonardoribeiro.reservafacil.utils.ToastUtils;

public class RestaurantRegister extends AppCompatActivity {
    @BindView(R.id.tb)
    Toolbar mToolbar;

    String restaurantEmail;
    String restaurantPassword;
    String restaurantPasswordConfirm;
    String restaurantName;
    String restaurantAddress;
    String restaurantPhone;

    @BindView(R.id.et_restaurant_name)
    EditText mRestaurantName;

    @BindView(R.id.et_restaurant_address)
    EditText mRestaurantAddress;

    @BindView(R.id.et_restaurant_email)
    EditText mRestaurantEmail;

    @BindView(R.id.et_restaurant_phone)
    EditText mRestaurantPhone;

    @BindView(R.id.et_restaurant_password)
    EditText mRestaurantPassword;

    @BindView(R.id.et_restaurant_password_confirm)
    EditText mRestaurantPasswordConfirm;

    private DatabaseReference mDatabaseRestaurant;


    private FirebaseAuth mFirebaseAuth;

    private static final int GALLERY_PICK = 1;

    @BindView(R.id.res_image)
    ImageView mResImage;

    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_register);

        mDatabaseRestaurant = FirebaseDatabase.getInstance().getReference().child("restaurant");

        mFirebaseAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

        //seta voltar da toolbar V

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reservar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //seta voltar da toolbar ^
    }

    public void handlerRequest(View v) {
        validateDataFromEditText();
    }

    private String editTextToString(EditText editText) {
        return editText.getText().toString().trim();
    }

    public void validateDataFromEditText() {
        restaurantName = editTextToString(mRestaurantName);
        restaurantAddress = editTextToString(mRestaurantAddress);
        restaurantPhone = editTextToString(mRestaurantPhone);
        restaurantPassword = editTextToString(mRestaurantPassword);
        restaurantPasswordConfirm = editTextToString(mRestaurantPasswordConfirm);
        restaurantEmail = editTextToString(mRestaurantEmail);

        if (android.text.TextUtils.isEmpty(restaurantName)) {
            mRestaurantName.setError("Nome do restaurante em branco.");
            mRestaurantName.requestFocus();
            return;
        }
        if (android.text.TextUtils.isEmpty(restaurantEmail)) {
            mRestaurantEmail.setError("Email do restaurante em branco.");
            mRestaurantEmail.requestFocus();
            return;
        }

        if (android.text.TextUtils.isEmpty(restaurantAddress)) {
            mRestaurantAddress.setError("Endereço do restaurante em branco.");
            mRestaurantAddress.requestFocus();
            return;
        }


        if (android.text.TextUtils.isEmpty(restaurantPhone)) {
            mRestaurantPhone.setError("Telefone do restaurante em branco.");
            mRestaurantPhone.requestFocus();
            return;
        }

        if (android.text.TextUtils.isEmpty(restaurantPassword)) {
            mRestaurantPassword.setError("Senha em branco.");
            mRestaurantPassword.requestFocus();
            return;
        }
        if (android.text.TextUtils.isEmpty(restaurantPasswordConfirm)) {
            mRestaurantPasswordConfirm.setError("Confirmaçao de senha em branco.");
            mRestaurantPasswordConfirm.requestFocus();
            return;
        }
        if (restaurantPassword.length() < 6) {
            mRestaurantPassword.setError("Sua senha deve ter no mínimo 6 caracteres.");
            mRestaurantPassword.requestFocus();
            return;
        }

        if (mUri == null) {
            new AlertDialog.Builder(RestaurantRegister.this)
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


        }
    }

    private void loginWithEmailAndPassword() {

        DialogFactory.createDialog(this, getString(R.string.enter), getString(R.string.validating_information));

        mFirebaseAuth.createUserWithEmailAndPassword(restaurantEmail, restaurantPassword)
                .addOnSuccessListener(RestaurantRegister.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userID = mFirebaseAuth.getCurrentUser().getUid();

                        if (mUri != null) {
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(mUri).build();
                            authResult.getUser().updateProfile(request);
                        }


                        Restaurant restaurant = new Restaurant();
                        restaurant.setKey(userID);
                        restaurant.setName(restaurantName);
                        restaurant.setEmail(restaurantEmail);

                        mDatabaseRestaurant.child(userID).setValue(restaurant).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DialogFactory.destroyDialog();
                                Intent intent = new Intent(RestaurantRegister.this, Main2ActivityRestaurant.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(RestaurantRegister.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        DialogFactory.destroyDialog();

                        ToastUtils.makeText(RestaurantRegister.this, e.getMessage());

                        if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                            ToastUtils.makeText(RestaurantRegister.this, getString(R.string.email_already_used_with_facebook_account));
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
                        into(mResImage);


            }

        }
    }

}
