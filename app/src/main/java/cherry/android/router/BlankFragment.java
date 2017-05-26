package cherry.android.router;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cherry.android.router.annotations.Route;
import cherry.android.router.api.Router;


/**
 * A simple {@link Fragment} subclass.
 */
@Route("fragment://blank")
public class BlankFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_0).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Router.build("activity://cherry/route1").open();
    }
}
