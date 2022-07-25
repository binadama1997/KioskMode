package kiosk.mode_single.purpose.app.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import kiosk.mode_single.purpose.app.R;

/**
 */
public class SettingFragment extends DialogFragment {
    public static final String LOCKED_BUNDLE_KEY = "locked_bundle_key";
    public static final String KIOSK_MODE_PASSWORD = "kiosk";

    private IActionHandler actionHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_fragment, container,
                false);
        Bundle bundle = getArguments();
        assert bundle != null;
        final boolean isLocked = bundle.getBoolean(LOCKED_BUNDLE_KEY);
        final int status = !isLocked ? R.string.setting_unlock_device : R.string.setting_lock_device;
        final int btnText = isLocked ? R.string.setting_unlock_device : R.string.setting_lock_device;
        final TextView enterPassword = rootView.findViewById(R.id.enter_password);
        TextView statusText = rootView.findViewById(R.id.status);
        Button dialogButton = rootView.findViewById(R.id.btn_lock_unlock);
        String thisStatusText = String.format("%s%s", getString(R.string.status_title), getString(status));
        statusText.setText(thisStatusText);
        dialogButton.setText(btnText);
        dialogButton.setOnClickListener(v -> {
            String text = enterPassword.getText().toString();
            if (!text.trim().equals(KIOSK_MODE_PASSWORD)) {
                Toast.makeText(getActivity(), R.string.invalid_password, Toast.LENGTH_LONG).show();
                return;
            }
            if (actionHandler != null) {
                actionHandler.isLocked(!isLocked);
            }
            dismiss();
        });
        Objects.requireNonNull(getDialog()).setTitle(R.string.setting_title);
        return rootView;
    }

    /**
     * set call back handler
     *
     * @param actionHandler helps return the call back
     */
    public void setActionHandler(IActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    /**
     * send call back
     */
    public interface IActionHandler {
        void isLocked(boolean isLocked);
    }
}