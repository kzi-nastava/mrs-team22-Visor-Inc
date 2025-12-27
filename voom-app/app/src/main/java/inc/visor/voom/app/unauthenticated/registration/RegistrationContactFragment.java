package inc.visor.voom.app.unauthenticated.registration;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;

import inc.visor.voom.app.R;

public class RegistrationContactFragment extends Fragment {

    RegistrationViewModel viewModel;
    TextInputEditText phoneNumberInput;
    TextInputEditText addressInput;
    ImageView imageView;
    Button buttonPrevious;
    Button buttonSignup;
    Button buttonProfileImage;
    ActivityResultLauncher<Intent> pickImageLauncher;


    public RegistrationContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumberInput = view.findViewById(R.id.phone_number_input);
        addressInput = view.findViewById(R.id.address_input);

        viewModel = new ViewModelProvider(requireParentFragment().requireParentFragment()).get(RegistrationViewModel.class);

        setupPhoneNumberInput();
        setupAddressInput();

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonPrevious = view.findViewById(R.id.fragment_registration_contact_previous);

        buttonPrevious.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_registrationContactFragment_to_registrationAccountFragment));

        buttonSignup = view.findViewById(R.id.fragment_registration_contact_signup);

        buttonSignup.setOnClickListener(v -> viewModel.setRegistrationComplete(true));

        buttonProfileImage = view.findViewById(R.id.upload_profile_image);

        buttonProfileImage.setOnClickListener(v -> openImagePicker());

        imageView = view.findViewById(R.id.image_preview);

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void setupPhoneNumberInput() {
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String phoneNumber = editable.toString();
                if (phoneNumber.isEmpty()) {
                    phoneNumberInput.setError("Email is required");
                } else if (phoneNumber.length() < 10) {
                    phoneNumberInput.setError("Phone number must be at least 10 numbers long");
                } else {
                    phoneNumberInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setPhoneNumber(charSequence.toString());
            }
        });
    }

    private void setupAddressInput() {
        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String address = editable.toString();
                if (address.isEmpty()) {
                    addressInput.setError("Email is required");
                } else if (address.length() < 10) {
                    addressInput.setError("Phone number must be at least 10 numbers long");
                } else {
                    addressInput.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setAddress(charSequence.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_contact, container, false);
    }
}