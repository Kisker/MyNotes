package net.learn2develop.mynotes.fragments;
import androidx.fragment.app.Fragment;;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.learn2develop.mynotes.R;
import net.learn2develop.mynotes.database.DataBaseHelper;
import net.learn2develop.mynotes.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ListFragment extends Fragment {

    private DataBaseHelper databaseHelper;

    // Container Activity must implement this interface
    public interface OnProductSelectedListener {
        void onProductSelected(int id);
    }

    OnProductSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            List<Product> list = getDatabaseHelper().getProductDao().queryForAll();

            adapter = new ArrayAdapter<Product>(getActivity(), R.layout.list_item, list);

            final ListView listView = (ListView)getActivity().findViewById(R.id.products);

            // Assign adapter to ListView
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Since we are working with a database, each element has a unique id
                    // so we need to see exactly which element we clicked on.
                    // We can do this by extracting the product from the list and getting its id
                    Product p = (Product) listView.getItemAtPosition(position);

                    listener.onProductSelected(p.getId());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnProductSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnProductSelectedListener");
        }
    }

    public DataBaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DataBaseHelper.class);
        }
        return databaseHelper;
    }
}
