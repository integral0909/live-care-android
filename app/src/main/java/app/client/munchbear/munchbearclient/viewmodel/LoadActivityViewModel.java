package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Roman H.
 */

public class LoadActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
