package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class KeywordFragment extends Fragment {
    private ListView listOptions;
    private Button buttonSelect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    public void setKeywordsInList(List<HashMap<String, String>>) {

    }

    private void initializeComponents(View view) {
        listOptions = view.findViewById(R.id.listOptions);
        buttonSelect = view.findViewById(R.id.buttonSelect);
    }

    private void registerListeners() {

    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    private class ListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
