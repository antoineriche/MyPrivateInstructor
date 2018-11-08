package com.antoineriche.privateinstructor.activities.item.pupil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFormItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PupilFormFragment extends AbstractFormItemFragment {

    private static int REQUEST_CODE_PLACE_AUTOCOMPLETE = 95;
    private static final int REQUEST_CODE_LOAD_IMAGE = 99;
    private static final int REQUEST_CODE_PIC_CROP = 97;
    private static final int PERMISSION_EXTERNAL_STORAGE = 87;

    private static final int MINIMAL_NAME_LENGTH = 3;
    private static final String PATH_PHOTOS_FOLDER = String.format(Locale.FRANCE, "/%s/pupils/photos/", "PrivateInstructor");

    private boolean imgChanged = false;
    private Place mPlace = null;

    public PupilFormFragment() {
    }

    public static PupilFormFragment newInstance(long pPupilId) {
        PupilFormFragment fragment = new PupilFormFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pPupilId);
        args.putBoolean(AbstractItemActivity.ARG_ITEM_EDITION, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static PupilFormFragment newInstance() {
        return new PupilFormFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(selectedImage, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CODE_PIC_CROP);
        }

        else if (requestCode == REQUEST_CODE_PIC_CROP && resultCode == RESULT_OK && null != data) {

            if (data != null) {

                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                selectedBitmap = getRoundedCroppedBitmap(selectedBitmap);

                imgChanged = true;
                ((ImageView) getView().findViewById(R.id.iv_pupil_pix)).setImageBitmap(selectedBitmap);
            }
        }

        else if (requestCode == REQUEST_CODE_PLACE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                TextView tvAddress = getView().findViewById(R.id.tv_pupil_address);
                if(tvAddress != null){
                    tvAddress.setText(place.getAddress());
                }
                mPlace = place;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(getClass().getSimpleName(), status.getStatusMessage());
                mPlace = null;
            } else if (resultCode == RESULT_CANCELED) {
                Log.e(getClass().getSimpleName(), "Error");
                mPlace = null;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab_pupil_load_pix).setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_EXTERNAL_STORAGE);
            } else {
                startActivityForResult(
                        new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        REQUEST_CODE_LOAD_IMAGE);
            }
        });

        view.findViewById(R.id.cv_address).setOnClickListener(v -> {
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(new AutocompleteFilter.Builder().setCountry("FR").build())
                        .build(getActivity());
            } catch (GooglePlayServicesRepairableException e) {
                Log.e("PupilFormFragment", "GooglePlayServicesRepairableException", e);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e("PupilFormFragment", "GooglePlayServicesNotAvailableException", e);
            }

            startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE);
        });

    }

    @Override
    protected Pupil extractItemFromView(View view) throws IllegalArgumentException {
        Pupil p;

        //FIXME
        if (mItemId != -1) {
            p = getItemFromDB(mListener.getDatabase(), mItemId);
        } else {
            p = new Pupil();
        }

        // Name
        Editable name = ((EditText) view.findViewById(R.id.et_pupil_first_name)).getText();
        if(!TextUtils.isEmpty(name) && name.toString().length() >= MINIMAL_NAME_LENGTH) {
            p.setFirstname(name.toString());
        } else {
            throw new IllegalArgumentException("Invalid first name");
        }

        name = ((EditText) view.findViewById(R.id.et_pupil_last_name)).getText();
        if(!TextUtils.isEmpty(name) && name.toString().length() >= MINIMAL_NAME_LENGTH) {
            p.setLastname(name.toString());
        } else {
            throw new IllegalArgumentException("Invalid last name");
        }

        // Class Level
        p.setClassLevel(((Spinner) view.findViewById(R.id.spinner_class_level)).getSelectedItemPosition());

        // Coordinates
        if(mPlace != null){
            p.setLocation(new Location(mPlace));
        }

        //TODO check phone validation
        p.setPhone(((EditText) view.findViewById(R.id.et_pupil_phone)).getText().toString());
        //TODO check phone validation
        p.setParentPhone(((EditText) view.findViewById(R.id.et_pupil_parent_phone)).getText().toString());

        // Hourly price
        try {
            p.setHourlyPrice(Double.parseDouble(((EditText) view.findViewById(R.id.et_pupil_hourly_price)).getText().toString()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid hourly price");
        }

        // Payment Type
        switch (((RadioGroup) view.findViewById(R.id.rg_pupil_payment_type)).getCheckedRadioButtonId()) {
            case R.id.rb_pupil_payment_agency:
                p.setPaymentType(Pupil.AGENCY);
                break;
            case R.id.rb_pupil_payment_black:
                p.setPaymentType(Pupil.BLACK);
                break;
            default:
                p.setPaymentType(Pupil.BLACK);
                break;
        }

        // Frequency
        switch (((RadioGroup) view.findViewById(R.id.rg_pupil_course_frequency)).getCheckedRadioButtonId()) {
            case R.id.rb_pupil_frequency_temporarily:
                p.setFrequency(Pupil.TEMPORARILY);
                break;
            case R.id.rb_pupil_frequency_occasionally:
                p.setFrequency(Pupil.OCCASIONALLY);
                break;
            case R.id.rb_pupil_frequency_regular:
                p.setFrequency(Pupil.REGULAR);
                break;
            default:
                p.setFrequency(Pupil.REGULAR);
                break;
        }

        // Gender
        switch (((RadioGroup) view.findViewById(R.id.rg_pupil_gender)).getCheckedRadioButtonId()) {
            case R.id.rb_pupil_gender_male:
                p.setGender(Pupil.GENDER_MALE);
                break;
            case R.id.rb_pupil_gender_female:
                p.setGender(Pupil.GENDER_FEMALE);
                break;
            default:
                p.setGender(Pupil.GENDER_MALE);
                break;
        }

        if(imgChanged){
            if(!TextUtils.isEmpty(p.getImgPath())){ new File(p.getImgPath()).delete(); }

            Bitmap pupilPix = ((BitmapDrawable) ((ImageView) getView().findViewById(R.id.iv_pupil_pix)).getDrawable()).getBitmap();
            p.setImgPath(copyPicture(pupilPix));
        }

        return p;
    }

    @Override
    protected void fillViewWithItem(View pView, Object item) {
        Pupil pupil = (Pupil) item;

        if(TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
            Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                    ContextCompat.getDrawable(getActivity(), R.drawable.man) :
                    ContextCompat.getDrawable(getActivity(), R.drawable.woman);

            ((ImageView) pView.findViewById(R.id.iv_pupil_pix)).setImageDrawable(pImg);
        } else {
            ((ImageView) pView.findViewById(R.id.iv_pupil_pix)).setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
        }

        ((EditText) pView.findViewById(R.id.et_pupil_first_name)).setText(pupil.getFirstname());
        ((EditText) pView.findViewById(R.id.et_pupil_last_name)).setText(pupil.getLastname());

        ((TextView) pView.findViewById(R.id.tv_pupil_address)).setText(
                pupil.getLocation() != null ? pupil.getLocation().getAddress() : "unknown location");
        ((EditText) pView.findViewById(R.id.et_pupil_phone)).setText(pupil.getPhone());
        ((EditText) pView.findViewById(R.id.et_pupil_parent_phone)).setText(pupil.getParentPhone());

        ((EditText) pView.findViewById(R.id.et_pupil_hourly_price)).setText(
                pupil.getFriendlyHourlyPrice().replace(",", "."));

        ((RadioButton) pView.findViewById(R.id.rb_pupil_gender_female)).setChecked(pupil.getGender() == Pupil.GENDER_FEMALE);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_gender_male)).setChecked(pupil.getGender() == Pupil.GENDER_MALE);

        ((RadioButton) pView.findViewById(R.id.rb_pupil_payment_agency)).setChecked(pupil.getPaymentType() == Pupil.AGENCY);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_payment_black)).setChecked(pupil.getPaymentType() == Pupil.BLACK);

        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_occasionally)).setChecked(pupil.getFrequency() == Pupil.OCCASIONALLY);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_regular)).setChecked(pupil.getFrequency() == Pupil.REGULAR);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_temporarily)).setChecked(pupil.getFrequency() == Pupil.TEMPORARILY);

        ((Spinner) pView.findViewById(R.id.spinner_class_level)).setSelection(pupil.getClassLevel());
    }

    @Override
    protected void initView(View view) {
        List<String> classLevels = Arrays.asList(getResources().getStringArray(R.array.pupil_class_levels));
        ArrayAdapter adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, classLevels);
        ((Spinner) view.findViewById(R.id.spinner_class_level)).setAdapter(adapter);
    }

    @Override
    protected void cleanView(View view) {
        ((ImageView) view.findViewById(R.id.iv_pupil_pix)).setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.man));

        ((EditText) view.findViewById(R.id.et_pupil_first_name)).setText(null);
        ((EditText) view.findViewById(R.id.et_pupil_last_name)).setText(null);

        //FIXME
        mPlace = null;
        ((TextView) view.findViewById(R.id.tv_pupil_address)).setText(null);

        ((EditText) view.findViewById(R.id.et_pupil_phone)).setText(null);
        ((EditText) view.findViewById(R.id.et_pupil_parent_phone)).setText(null);

        ((EditText) view.findViewById(R.id.et_pupil_hourly_price)).setText(null);

        ((RadioButton) view.findViewById(R.id.rb_pupil_payment_black)).setChecked(true);
        ((RadioButton) view.findViewById(R.id.rb_pupil_frequency_regular)).setChecked(true);
    }

    @Override
    protected Pupil getItemFromDB(SQLiteDatabase database, long mItemId) {
        return PupilTable.getPupilWithId(database, mItemId);
    }

    @Override
    protected int layout() {
        return R.layout.pupil_form;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the thing
                    startActivityForResult(
                            new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            REQUEST_CODE_LOAD_IMAGE);
                } else {
                    // permission denied, boo!
                }
            }
        }
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }

    public String copyPicture(Bitmap pPicture) {
        String path = null;

        File dst = new File(Environment.getExternalStorageDirectory() + PATH_PHOTOS_FOLDER);

        if(!dst.exists() && !dst.mkdirs()){
            Toast.makeText(getActivity(), "External storage access denied", Toast.LENGTH_SHORT).show();
        }

        else {

            path = String.format(Locale.FRANCE, "%s/%s.png", dst.getPath(), String.valueOf(System.currentTimeMillis()));

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
                pPicture.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                Log.e("Exception while copying picture", e.getMessage(), e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("Exception while copying picture", e.getMessage(), e);
                }
            }
        }
        return path;
    }
}
