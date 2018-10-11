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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PupilFormFragment extends AbstractFormItemFragment {

    private static final int MINIMAL_NAME_LENGTH = 3;
    private static final int RESULT_LOAD_IMAGE = 99;
    private static final int RESULT_PIC_CROP = 97;
    private static final String PATH_PHOTOS_FOLDER = String.format(Locale.FRANCE, "/%s/pupils/photos/", "PrivateInstructor");
    private static final int PERMISSION_EXTERNAL_STORAGE = 87;

    private boolean imgChanged = false;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> classLevels = Arrays.asList(getResources().getStringArray(R.array.pupil_class_levels));
        ArrayAdapter adapter =
                new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, classLevels);
        ((Spinner) getView().findViewById(R.id.spinner_class_level)).setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
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
            startActivityForResult(cropIntent, RESULT_PIC_CROP);

        }

        if (requestCode == RESULT_PIC_CROP && resultCode == getActivity().RESULT_OK && null != data) {

            if (data != null) {

                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                selectedBitmap = getRoundedCroppedBitmap(selectedBitmap);

                imgChanged = true;
                ((ImageView) getView().findViewById(R.id.iv_pupil_pix)).setImageBitmap(selectedBitmap);

//                try {
//                    mPupil.setImgPath( copyPicture(selectedBitmap, mPupil.getId()));
//                } catch (IOException e) {
//                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
//                }
//

//                ((ImageView) mContentView.findViewById(R.id.avatar)).setImageBitmap(selectedBitmap);
            }
        }
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
        //TODO check address validation
        p.setAddress(((EditText) view.findViewById(R.id.et_pupil_address)).getText().toString());
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
                p.setFrequency(Pupil.OCCASIONALY);
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

        ((EditText) pView.findViewById(R.id.et_pupil_address)).setText(pupil.getAddress());
        ((EditText) pView.findViewById(R.id.et_pupil_phone)).setText(pupil.getPhone());
        ((EditText) pView.findViewById(R.id.et_pupil_parent_phone)).setText(pupil.getParentPhone());

        ((EditText) pView.findViewById(R.id.et_pupil_hourly_price)).setText(
                String.format(Locale.FRANCE, "%.2f", (pupil.getHourlyPrice())).replace(",", "."));

        ((RadioButton) pView.findViewById(R.id.rb_pupil_gender_female)).setChecked(pupil.getGender() == Pupil.GENDER_FEMALE);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_gender_male)).setChecked(pupil.getGender() == Pupil.GENDER_MALE);

        ((RadioButton) pView.findViewById(R.id.rb_pupil_payment_agency)).setChecked(pupil.getPaymentType() == Pupil.AGENCY);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_payment_black)).setChecked(pupil.getPaymentType() == Pupil.BLACK);

        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_occasionally)).setChecked(pupil.getFrequency() == Pupil.OCCASIONALY);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_regular)).setChecked(pupil.getFrequency() == Pupil.REGULAR);
        ((RadioButton) pView.findViewById(R.id.rb_pupil_frequency_temporarily)).setChecked(pupil.getFrequency() == Pupil.TEMPORARILY);

        ((Spinner) pView.findViewById(R.id.spinner_class_level)).setSelection(pupil.getClassLevel());

        pView.findViewById(R.id.fab_pupil_load_pix).setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_EXTERNAL_STORAGE);
            } else {
                startActivityForResult(
                        new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void cleanView(View view) {
        ((ImageView) view.findViewById(R.id.iv_pupil_pix)).setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.man));

        ((EditText) view.findViewById(R.id.et_pupil_first_name)).setText(null);
        ((EditText) view.findViewById(R.id.et_pupil_last_name)).setText(null);

        ((EditText) view.findViewById(R.id.et_pupil_address)).setText(null);
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
                            RESULT_LOAD_IMAGE);
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
