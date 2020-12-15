package net.learn2develop.mynotes.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.learn2develop.mynotes.MainActivity;
import net.learn2develop.mynotes.R;
import net.learn2develop.mynotes.model.Product;
import java.sql.SQLException;
import java.util.Calendar;

public class DetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static int NOTIFICATION_ID = 1;

    private Product product = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (product == null) { product = ((MainActivity)getActivity()).getDatabaseHelper().getProductDao().queryForId(0); }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            product = new Product();
            product.setId(savedInstanceState.getInt("id"));
            product.setName(savedInstanceState.getString("name"));
            product.setDescription(savedInstanceState.getString("description"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceState.putInt("id", product.getId());
            savedInstanceState.putString("name", product.getName());
            savedInstanceState.putString("description", product.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(product.getName());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(product.getDescription());

        Button buttonDate = view.findViewById(R.id.buttonDate);

        // When we specify the DatePickerFragment class, only then do we start implementing the Interface.
        // Without these implementations, we would not be able to open a new input window by clicking the Date button.
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");

            }
        });

        return view;
    }
    // We always add setProduct, to return to us and save the selected product at the same time
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * When we add a new element to the Toolbar we need to delete the previous elements
     * so we call menu.clear () and add new Toolbar elements
     * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    //Ova metoda sluzi za izmenu i cuvanje podataka koje smo vec uneli klikom na Add dugme.
    //Svaki put kada izvrsimo i sacuvamo akciju Add, mozemo otvoriti nas sacuvan fajl i ga nadknadno izmenimo sa doUpdate

    private void doUpdateElement(){
        if (product != null){
            EditText name = (EditText) getActivity().findViewById(R.id.name);
            product.setName(name.getText().toString());

            EditText description = (EditText) getActivity().findViewById(R.id.description);
            product.setDescription(description.getText().toString());

            try {
                ((MainActivity) getActivity()).getDatabaseHelper().getProductDao().update(product);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getActivity().onBackPressed();
        }
    }

    private void doRemoveElement(){
        if (product != null) {
            try {
                ((MainActivity) getActivity()).getDatabaseHelper().getProductDao().delete(product);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getActivity().onBackPressed();
        }
    }

    /**
     * We add an element to the fragment to delete the element and to change the data
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                doRemoveElement();
                break;
            case R.id.update:
                doUpdateElement();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //  Within DetailsFragment, we specify the date in this case. DatePickerFragment
    // always inherits the DialogFragment and we implement a call to the interface, so that by clicking on Date we get a new window
    // with selectable date.
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Toast.makeText(getActivity(), dayOfMonth + "/" + (month + 1) + "/" + year + "", Toast.LENGTH_SHORT).show();
        }
    }
}
