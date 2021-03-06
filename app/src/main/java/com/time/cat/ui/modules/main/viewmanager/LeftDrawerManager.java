package com.time.cat.ui.modules.main.viewmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.APImodel.User;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.network.RetrofitHelper;
import com.time.cat.ui.modules.about.AboutActivity;
import com.time.cat.ui.modules.achievement.AchievementActivity;
import com.time.cat.ui.modules.main.MainActivity;
import com.time.cat.ui.modules.pomodoro.PomodoroActivity;
import com.time.cat.ui.modules.setting.SettingActivity;
import com.time.cat.ui.modules.statistic.StatisticalActivity;
import com.time.cat.ui.modules.user.LoginActivity;
import com.time.cat.ui.modules.user.UserDetailActivity;
import com.time.cat.ui.widgets.animation.ViewHelper;
import com.time.cat.ui.modules.theme.DialogThemeFragment;
import com.time.cat.data.model.Converter;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.source.AvatarManager;
import com.time.cat.util.view.IconUtil;
import com.time.cat.util.view.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription 侧滑栏
 */
public class LeftDrawerManager implements Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener {
    public static final int HOME = -1;
    public static final int USER_ADD = -2;
    public static final int SCHEDULES = 0;
    public static final int ROUTINES = 1;
    public static final int PLANS = 2;
    public static final int NOTES = 3;
    public static final int MEDICINES = 4;
    public static final int USERS = 5;
    public static final int THEME = 6;
    public static final int SETTINGS = 7;
    public static final int ABOUT = 8;
    public static final int PHARMACIES = 9;
    public static final int CALENDAR = 10;
    public static final int ACHIEVEMENTS = 11;
    public static final int STATISTICS = 12;
    public static final int POMODOROS = 13;

    private static final String TAG = "LeftDrawerManager";
    private static final int REQUEST_LOGIN = 0;
    private AccountHeader headerResult = null;
    private Drawer drawer = null;
    private Toolbar toolbar;
    private MainActivity mainActivity;
    private DBUser currentUser;

    public LeftDrawerManager(MainActivity activity, Toolbar toolbar) {
        this.toolbar = toolbar;
        this.mainActivity = activity;
    }

    public void init(Bundle savedInstanceState) {

        boolean isPharmaEnabled = TimeCatApp.isPharmaModeEnabled(mainActivity);

        ArrayList<IProfile> profiles = new ArrayList<>();
        profiles.add(new ProfileSettingDrawerItem()
                .withName("添加用户")
                .withDescription("用户列表")
                .withIcon(new IconicsDrawable(mainActivity, GoogleMaterial.Icon.gmd_account_add).sizeDp(24).paddingDp(5).colorRes(R.color.dark_grey_home))
                .withIdentifier(USER_ADD));
        for (DBUser p : DB.users().findAll()) {
            Log.d("LeftDrawer", "Adding user to getDrawer: " + p.name());
            profiles.add(new ProfileDrawerItem()
                    .withIdentifier(p.id().intValue())
                    .withName(p.name())
                    .withEmail(p.getEmail())
                    .withIcon(AvatarManager.res(p.avatar())));
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(mainActivity)
                .withHeaderBackground(R.drawable.drawer_header)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .withCompactStyle(false)
                .withProfiles(profiles)
                .withAlternativeProfileHeaderSwitching(false)
                .withOnlyMainProfileImageVisible(true)
                .withThreeSmallProfileImages(false)
                .withOnAccountHeaderListener(this)
                .withSavedInstance(savedInstanceState)
                .build();
        Drawable noteIcon = mainActivity.getResources().getDrawable(R.drawable.ic_notes_black_24dp);
        noteIcon.setAlpha(110);
        Drawable pomodoroIcon = mainActivity.getResources().getDrawable(R.drawable.ic_tomato_black_24dp);
//        pomodoroIcon.setAlpha(110);
        Drawable statisticsIcon = mainActivity.getResources().getDrawable(R.drawable.ic_statistics_black_24dp);
        statisticsIcon.setAlpha(110);
        Drawable achievementsIcon = mainActivity.getResources().getDrawable(R.drawable.ic_achievements_black_24dp);
        achievementsIcon.setAlpha(110);
        //Create the getDrawer
        drawer = new DrawerBuilder()
                .withActivity(mainActivity)
                .withFullscreen(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_home_option)
                                .withIcon(IconUtil.icon(mainActivity, GoogleMaterial.Icon.gmd_home, R.color.black).alpha(110))
                                .withIdentifier(HOME),
                        new PrimaryDrawerItem().withName(R.string.drawer_login_option)
                                .withIcon(IconUtil.icon(mainActivity, CommunityMaterial.Icon.cmd_account_multiple, R.color.black).alpha(110))
                                .withIdentifier(USERS),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_schedules_option)
                                .withIcon(IconUtil.icon(mainActivity, GoogleMaterial.Icon.gmd_calendar, R.color.black).alpha(110))
                                .withIdentifier(SCHEDULES),
                        new PrimaryDrawerItem().withName(R.string.drawer_routines_option)
                                .withIcon(IconUtil.icon(mainActivity, GoogleMaterial.Icon.gmd_alarm, R.color.black).alpha(110))
                                .withIdentifier(ROUTINES),
                        new PrimaryDrawerItem().withName(R.string.drawer_notes_option)
                                .withIcon(noteIcon)
                                .withIdentifier(NOTES),
                        new PrimaryDrawerItem().withName(R.string.drawer_plans_option)
                                .withIcon(IconUtil.icon(mainActivity, GoogleMaterial.Icon.gmd_airplanemode_active, R.color.black).alpha(110))
                                .withIdentifier(PLANS),
                        new PrimaryDrawerItem().withName(R.string.drawer_pomodoros_option)
                                .withIcon(pomodoroIcon)
                                .withIdentifier(POMODOROS),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withName(R.string.drawer_statistics_option)
                                .withIcon(statisticsIcon)
                                .withIdentifier(STATISTICS),
                        new PrimaryDrawerItem().withName(R.string.drawer_achievement_option)
                                .withIcon(achievementsIcon)
                                .withIdentifier(ACHIEVEMENTS),
                        new PrimaryDrawerItem().withName(R.string.drawer_theme_option)
                                .withIcon(IconUtil.icon(mainActivity, GoogleMaterial.Icon.gmd_pin_assistant, R.color.black).alpha(110))
                                .withIdentifier(THEME),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings_option)
                                .withIcon(IconUtil.icon(mainActivity, CommunityMaterial.Icon.cmd_settings, R.color.black).alpha(130))
                                .withIdentifier(SETTINGS),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withName(R.string.drawer_about_option)
                                .withIcon(IconUtil.icon(mainActivity, CommunityMaterial.Icon.cmd_information, R.color.black).alpha(110))
                                .withIdentifier(ABOUT))
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        View mContent = drawer.getDrawerLayout().getChildAt(0);
                        View mMenu = drawerView;
                        float scale = 1 - slideOffset;
//                        float rightScale = 0.8f + scale * 0.2f;
//                        float leftScale = 1 - 0.3f * scale;
//        Log.e(TAG, "\nscale:" + scale + "\nleftScale:" + leftScale + "\nrightScale:" + rightScale);

//                        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                        ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));

                        mContent.invalidate();
                    }
                })
                .withDelayOnDrawerClose(0)
                .withStickyFooterShadow(true)
                .withScrollToTopAfterClick(true)
                .withSliderBackgroundColor(Color.TRANSPARENT)
                .withSavedInstance(savedInstanceState)
                .build();

        DBUser u = DB.users().getActive();
        headerResult.setActiveProfile(u.id().intValue(), false);
        updateHeaderBackground(u);
        drawer.setStatusBarColor(u.color());
        drawer.getDrawerLayout().setClipToPadding(true);
        drawer.getDrawerLayout().setFitsSystemWindows(false);

        onPharmacyModeChanged(isPharmaEnabled);

    }





    //-//<Drawer.OnDrawerItemClickListener>---------------------------------------------------------
    @Override
    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {

        int identifier = iDrawerItem.getIdentifier();

        switch (identifier) {
            case HOME:
//                mainActivity.showPagerItem(0, false);
                break;
            case USERS:
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivityForResult(intent, REQUEST_LOGIN);
                mainActivity.overridePendingTransition(R.anim.push_left_to_right, R.anim.push_right_to_left);
                drawer.setSelection(HOME, false);
                break;

            case SCHEDULES:
                mainActivity.showPagerItem(0, false);
                break;
            case ROUTINES:
                mainActivity.showPagerItem(1, false);
                break;
            case NOTES:
                mainActivity.showPagerItem(2, false);
                break;
            case PLANS:
                mainActivity.showPagerItem(3, false);
                break;
            case CALENDAR:
//                launchActivity(new Intent(mainActivity, CalendarActivity.class));
                drawer.setSelection(HOME, false);
                break;
            case POMODOROS:
                launchActivity(new Intent(mainActivity, PomodoroActivity.class));
                break;

            case STATISTICS:
                launchActivity(new Intent(mainActivity, StatisticalActivity.class));
                drawer.setSelection(HOME, false);
                break;
            case ACHIEVEMENTS:
                launchActivity(new Intent(mainActivity, AchievementActivity.class));
                drawer.setSelection(HOME, false);
                break;
            case THEME:
                //mainActivity.showTutorial();
//                launchActivity(new Intent(mainActivity, MaterialIntroActivity.class));
                DialogThemeFragment themeDialog = new DialogThemeFragment();
                themeDialog.setClickListener(mainActivity);
                themeDialog.show(mainActivity.getSupportFragmentManager(), "theme");
                drawer.setSelection(HOME, false);
                break;
            case SETTINGS:
                launchActivity(new Intent(mainActivity, SettingActivity.class));
                drawer.setSelection(HOME, false);
                break;

            case ABOUT:
                launchActivity(new Intent(mainActivity, AboutActivity.class));
                drawer.setSelection(HOME, false);
                break;
            default:
                return false;
        }
//        drawer.closeDrawer();
        return true;
    }
    //-//</Drawer.OnDrawerItemClickListener>--------------------------------------------------------


    public void onPharmacyModeChanged(boolean enabled) {
        PrimaryDrawerItem item = (PrimaryDrawerItem) drawer.getDrawerItem(SCHEDULES);
        BadgeStyle bs = new BadgeStyle();
        if (enabled) {
            addCalendarItem();
            Drawable bg = new IconicsDrawable(mainActivity).icon(GoogleMaterial.Icon.gmd_check).color(mainActivity.getResources().getColor(R.color.dark_grey_text)).sizeDp(18);
            bs.withBadgeBackground(bg);
        } else {
            drawer.removeItem(CALENDAR);
            bs.withBadgeBackground(new ColorDrawable(Color.TRANSPARENT));
        }
        item.withBadgeStyle(bs);
        item.withBadge(" ");
        drawer.updateItem(item);
    }

    public void onPagerPositionChange(int pagerPosition) {
        Log.d("LeftDrawer", "onPagerPositionChange: " + pagerPosition);
        switch (pagerPosition) {
            case 0:
                drawer.setSelection(SCHEDULES, false);
                break;
            case 1:
                drawer.setSelection(ROUTINES, false);
                break;
            case 2:
                drawer.setSelection(NOTES, false);
                break;
            case 3:
                drawer.setSelection(PLANS, false);
                break;
        }
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

        if (profile instanceof ProfileSettingDrawerItem) {
            Intent intent = new Intent(mainActivity, UserDetailActivity.class);
            launchActivity(intent);
            return true;
        } else {
            Long id = (long) profile.getIdentifier();
            DBUser dbUser = DB.users().findById(id);
            boolean isActive = DB.users().isActive(dbUser);
            if (isActive) {
                Intent intent = new Intent(mainActivity, UserDetailActivity.class);
                intent.putExtra("user_id", id);
                launchActivity(intent);
            } else {
//                LogUtil.e("login dbUser -->" + dbUser.toString());
                RetrofitHelper.getUserService().login(Converter.toAPIUser(dbUser)) //获取Observable对象
                        .compose(mainActivity.bindToLifecycle())
                        .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                        .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                        .doOnNext(new Action1<User>() {
                            @Override
                            public void call(User user) {
                                //保存用户信息到本地
                                DB.users().updateActiveUserAndFireEvent(dbUser, user);
                                Log.i(TAG, user.toString());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                //请求失败
                                LogUtil.e(e.toString());
                                ToastUtil.e("登录失败");
                            }

                            @Override
                            public void onNext(User user) {
                                //请求成功
                                Log.i(TAG, "登录成功" + user.toString());
                                ToastUtil.ok("登录成功");
                            }
                        });
                // 不管登录是否成功，都更新activeUser
                DB.users().setActive(dbUser);
                updateHeaderBackground(dbUser);
                getDrawer().closeDrawer();
                return true;
            }
        }
        return false;
    }

    public void updateHeaderBackground(DBUser u) {
        currentUser = u;
        LayerDrawable layers = (LayerDrawable) headerResult.getHeaderBackgroundView().getDrawable();
        ColorDrawable color = (ColorDrawable) layers.findDrawableByLayerId(R.id.color_layer);
        color.setColor(ScreenUtil.equivalentNoAlpha(u.color(), 1f));
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public AccountHeader header() {
        return headerResult;
    }

    public void onActivityResume(DBUser u) {

        currentUser = u;

        List<DBUser> users = DB.users().findAll();
        ArrayList<IProfile> profiles = headerResult.getProfiles();
        ArrayList<IProfile> toRemove = new ArrayList<>();
        if (users.size() != profiles.size()) {
            for (IProfile pr : profiles) {
                Long id = Long.valueOf(pr.getIdentifier());
                boolean remove = true;
                for (DBUser pat : users) {
                    if (pat.id().equals(id)) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    toRemove.add(pr);
                }
            }
            for (IProfile pr : toRemove) {
                headerResult.removeProfile(pr);
            }
        }

        headerResult.setActiveProfile(u.id().intValue(), false);

        if (u != null && !u.equals(currentUser) || header().getActiveProfile().getIcon().getIconRes() != AvatarManager.res(u.avatar())) {
            headerResult.setActiveProfile(u.id().intValue(), false);
            IProfile profile = headerResult.getActiveProfile();
            profile.withIcon(AvatarManager.res(u.avatar()));
            headerResult.updateProfile(profile);
        }
        updateHeaderBackground(u);
    }

    public void onUserCreated(DBUser u) {

        IProfile profile = createProfile(u);
        headerResult.addProfiles(profile);
    }

    public void onUserUpdated(DBUser u) {
        IProfile profile = createProfile(u);
        headerResult.updateProfile(profile);

    }

    private void addCalendarItem() {
        drawer.addItemAtPosition(new PrimaryDrawerItem()
                .withName("Description")
                .withIcon(IconUtil.icon(mainActivity, CommunityMaterial.Icon.cmd_calendar_check, R.color.black).alpha(110))
                .withEnabled(true)
                .withIdentifier(CALENDAR), 7);
    }

    private void launchActivity(Intent i) {
        mainActivity.startActivity(i);
        mainActivity.overridePendingTransition(R.anim.push_left_to_right, R.anim.push_right_to_left);
    }

    private IProfile createProfile(DBUser u) {
        return new ProfileDrawerItem()
                .withIdentifier(u.id().intValue())
                .withName(u.name())
                .withEmail(u.getEmail())
                .withIcon(AvatarManager.res(u.avatar()));
    }
}