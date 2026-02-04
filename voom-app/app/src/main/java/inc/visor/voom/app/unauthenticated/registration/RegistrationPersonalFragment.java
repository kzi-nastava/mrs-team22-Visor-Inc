package inc.visor.voom.app.unauthenticated.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import inc.visor.voom.app.R;

public class RegistrationPersonalFragment extends Fragment {

    RegistrationViewModel viewModel;
    TextInputEditText firstNameInput;
    TextInputEditText lastNameInput;
    TextInputEditText birthDateInput;
    Button buttonNext;
    TextView buttonLogin;

    public RegistrationPersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameInput = view.findViewById(R.id.first_name_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        birthDateInput = view.findViewById(R.id.birth_date_input);

        viewModel = new ViewModelProvider(requireParentFragment().requireParentFragment()).get(RegistrationViewModel.class);

        setupFirstNameInput();
        setupLastNameInput();
        setupBirthDateInput();

        buttonNext = view.findViewById(R.id.fragment_registration_personal_next);

        buttonNext.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_registrationPersonalFragment_to_registrationAccountFragment));

        buttonLogin = view.findViewById(R.id.login);

        buttonLogin.setOnClickListener(v -> viewModel.setRegistrationComplete(true));

    }

    private void setupFirstNameInput() {
        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String result = editable.toString();
                if (!result.isEmpty()) {
                    firstNameInput.setError(null);
                } else {
                    firstNameInput.setError("First name cannot be empty");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setFirstName(charSequence.toString());
            }
        });
    }

    private void setupLastNameInput() {
        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final String result = editable.toString();
                if (!result.isEmpty()) {
                    lastNameInput.setError(null);
                } else {
                    firstNameInput.setError("First name cannot be empty");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setLastName(charSequence.toString());
            }
        });
    }

    private void setupBirthDateInput() {

        final CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(Calendar.getInstance().getTimeInMillis()) // Limit to today
                .build();

        final MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints)
                .build();

        birthDateInput.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            final LocalDateTime birthDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(selection), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            birthDateInput.setText(formatter.format(birthDate));
            viewModel.setBirthDate(birthDate);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_personal, container, false);
    }
}