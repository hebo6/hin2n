package wang.switchy.hin2n.template;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wang.switchy.hin2n.R;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

/**
 * Created by janiszhang on 2018/4/13.
 */

public class CommonTitleTemplate extends BaseTemplate {
    private Context mContext;
    /**
     * 整个页面的视图View
     */
    private LinearLayout mPageView;

    /**
     * 标题栏布局区域
     */
    private RelativeLayout mTitleLayout;

    /**
     * 内容显示区
     */
    private FrameLayout mContainerLayout;

    public final ImageView mLeftImg;
    public final ImageView mRightImg;
    private final TextView mTitleText;


    public CommonTitleTemplate(Context context, String title) {
        super(context);

        this.mContext = context;

        mPageView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.main_template_view, null);

        mTitleLayout = (RelativeLayout) mPageView.findViewById(R.id.rl_title);
        mContainerLayout = (FrameLayout) mPageView.findViewById(R.id.fl_container);
        FrameLayout mTitleContainer = (FrameLayout) mPageView.findViewById(R.id.fl_title_container);
        
        ViewCompat.setOnApplyWindowInsetsListener(mPageView, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            // Apply top inset to the title container (Status Bar)
            if (mTitleContainer != null) {
                mTitleContainer.setPadding(
                        mTitleContainer.getPaddingLeft(),
                        systemBars.top,
                        mTitleContainer.getPaddingRight(),
                        mTitleContainer.getPaddingBottom()
                );
            }

            // Apply bottom inset to the content container (Navigation Bar + Keyboard)
            // We use the maximum of systemBars.bottom and ime.bottom to handle both cases
            // (Standard nav bar vs Keyboard visible)
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            mContainerLayout.setPadding(
                    mContainerLayout.getPaddingLeft(),
                    mContainerLayout.getPaddingTop(),
                    mContainerLayout.getPaddingRight(),
                    bottomPadding
            );

            return WindowInsetsCompat.CONSUMED;
        });

        mTitleText = (TextView) mPageView.findViewById(R.id.tv_title);

        mLeftImg = (ImageView) mPageView.findViewById(R.id.iv_left_img);
        mRightImg = (ImageView) mPageView.findViewById(R.id.iv_right_img);

        mTitleText.setText(title);
    }

    @Override
    public void setContentView(View contentView) {
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(contentView, contentViewLayoutParams);
    }

    public void setTitleText(String titleText) {
        mTitleText.setText(titleText);
    }
    public void setTitleText(int resid) {
        mTitleText.setText(resid);
    }

    @Override
    public View getPageView() {
        return mPageView;
    }
}
