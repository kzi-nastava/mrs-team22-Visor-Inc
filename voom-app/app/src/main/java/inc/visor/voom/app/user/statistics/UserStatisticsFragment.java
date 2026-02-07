package inc.visor.voom.app.user.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import inc.visor.voom.app.databinding.FragmentUserStatisticsBinding;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.component.BaseStatisticsFragment;
import inc.visor.voom.app.user.statistics.dto.ReportResponseDto;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserStatisticsFragment extends BaseStatisticsFragment {

    private UserStatisticsViewModel viewModel;
    private DataStoreManager storeManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private Long currentUserId = null;
    private boolean isSuspended = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentUserStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserStatisticsViewModel.class);
        storeManager = DataStoreManager.getInstance(requireContext());

        disposables.add(
                storeManager.getUserId()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> currentUserId = id)
        );

        viewModel.getReport().observe(getViewLifecycleOwner(), dto -> {
            if (dto != null) {
                populateUi(dto);
            } else {
                resetUi();
            }
        });
    }

    @Override
    protected void onGenerate(String from, String to) {
        if (currentUserId == null) return;
        viewModel.loadReport(from, to, currentUserId.intValue());
    }

    @Override
    protected String getMoneySummaryLabel() {
        return "Total expenses";
    }

    @Override
    protected String getMoneyChartLabel() {
        return "Expenses per day";
    }

    @Override
    protected boolean canGenerateInternal() {
        return currentUserId != null && !isSuspended;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }
}
