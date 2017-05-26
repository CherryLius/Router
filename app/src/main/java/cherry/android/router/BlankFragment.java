package cherry.android.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cherry.android.router.api.Router;
import cherry.android.router.api.utils.Logger;


public class BlankFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_0).setOnClickListener(this);
        Logger.e("Test", "onViewCreated");
    }

    @Override
    public void onClick(View v) {
        Router.build("test/test?id=1&name=Tom&page=10#1")
                .requestCode(100)
                .open(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("Test", "fragment onActivityResult:" + requestCode);
    }
}
