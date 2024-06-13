package com.qb.hotelTV.Activity.Hospital.fgm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.databinding.FragmentWebBinding;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentWebBinding fragmentWebBinding;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private FocusScaleListener focusScaleListener = new FocusScaleListener();
    public WebFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebFragment newInstance(String param1, String param2) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentWebBinding = fragmentWebBinding.inflate(inflater,container,false);
        String serverAddress = getActivity().getIntent().getStringExtra("serverAddress");
        String tenant = getActivity().getIntent().getStringExtra("tenant");
        int id = getActivity().getIntent().getIntExtra("id",-1);
        if (id != -1){
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    String strHtml = cms.get(0).getContent();
//                            strHtml = "";
                    String strTitle = cms.get(0).getTitle();
//                    EventBus.getDefault().post(new TitleEvent(strTitle));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (strHtml != null && !strHtml.equals("")){
                                fragmentWebBinding.hospitalWebContact.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
                                fragmentWebBinding.hospitalWebContact.requestFocus();
                                fragmentWebBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);

                            }else {
                                fragmentWebBinding.hospitalWebContact.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onCmsMessageFailure(int code, String msg) {

                }
            });


        }


        return fragmentWebBinding.getRoot();
    }

    public WebView getWebView() {
        return fragmentWebBinding.hospitalWebContact;
    }


}