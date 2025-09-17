package com.m.apk.extractor.skech;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import android.content.pm.PackageManager;
import android.Manifest;
import android.content.pm.ApplicationInfo;
import com.m.apk.extractor.skech.helpers.toast.ToastHelper;
import com.m.apk.extractor.skech.utils.soundPool.SoundPoolManager;


public class MainActivity extends Activity {
	
	private ToastHelper toastHelper;
	private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1001;
	private SoundPoolManager soundPoolManager;
	private MainBinding binding;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		binding.buttonExtract.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				try {
					soundPoolManager.playSound("click");
				} catch (Exception e) {
					Log.e("SOUND_ERROR", e.getMessage());
				}
				
				final String packageName = binding.edittextPackage.getText().toString().trim();
				
				if (packageName.isEmpty()) {
					binding.textviewWarn.setVisibility(View.VISIBLE);
					binding.textviewWarn.setText("Empty package name.");
					return;
				}
				
				try {
					binding.textviewWarn.setVisibility(View.GONE);
					
					ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);
					final String apkPath = appInfo.sourceDir;
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
						if (!_checkStoragePermission()) {
							return;
						}
					}
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							FileInputStream in = null;
							FileOutputStream out = null;
							
							try {
								File downloadsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "apks");
								String dirPath = downloadsDir.getAbsolutePath();
								
								FileUtil.makeDir(dirPath);
								final File sourceFile = new File(apkPath);
								
								if (!sourceFile.exists()) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (toastHelper != null) {
												toastHelper.showToast("APK file not found!");
											}
										}
									});
									return;
								}
								
								final String fileName = packageName + ".apk";
								final String destPath = new File(downloadsDir, fileName).getAbsolutePath();
								
								in = new FileInputStream(sourceFile);
								out = new FileOutputStream(destPath);
								
								byte[] buffer = new byte[8192];
								int length;
								while ((length = in.read(buffer)) > 0) {
									out.write(buffer, 0, length);
								}
								in.close();
								out.close();
								
								final File destFile = new File(destPath);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (destFile.exists() && destFile.length() == sourceFile.length()) {
											if (toastHelper != null) {
												toastHelper.showToast("APK saved in: Downloads/apks/" + fileName);
											}
										} else {
											if (toastHelper != null) {
												toastHelper.showToast("Error: Incomplete copy!");
											}
										}
									}
								});
							} catch (final IOException e) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (toastHelper != null) {
											toastHelper.showToast("File error: " + e.getMessage());
										}
									}
								});
							} finally {
								try {
									if (in != null) in.close();
									if (out != null) out.close();
								} catch (IOException e) {
								}
							}
						}
					}).start();
					
				} catch (PackageManager.NameNotFoundException e) {
					binding.textviewWarn.setVisibility(View.VISIBLE);
					binding.textviewWarn.setText("App not found");
				} catch (SecurityException e) {
					if (toastHelper != null) {
						toastHelper.showToast("Permission denied!");
					}
				} catch (Exception e) {
					if (toastHelper != null) {
						toastHelper.showToast("Error: " + e.getMessage());
					}
				}
				
			}
		});
	}
	
	private void initializeLogic() {
		this.soundPoolManager = new SoundPoolManager(this);
		// this.permissionUtils = new PermissionUtils();
		ActionBar actionBar = getActionBar();
		soundPoolManager.createSoundPool("efeitos", 5);
		soundPoolManager.loadSound("efeitos", "click", R.raw.click_sound);
		try{
			binding.textviewExtractor.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lilita_one_regular.ttf"), 1);
			binding.textviewWarn.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lilita_one_regular.ttf"), 1);
			binding.edittextPackage.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lilita_one_regular.ttf"), 1);
			binding.buttonExtract.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lilita_one_regular.ttf"), 1);
			binding.textviewVersion.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/lilita_one_regular.ttf"), 1);
		}catch(Exception e){
			
		}
		binding.textviewWarn.setVisibility(View.GONE);
		this.toastHelper = new ToastHelper(getApplicationContext());
		if (actionBar != null) {
			actionBar.setTitle("Apk Extractor");
			actionBar.setSubtitle("Extract apks on android!");
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.soundPoolManager != null) {
			this.soundPoolManager.release();
			this.soundPoolManager = null;
		}
		
		if (this.toastHelper != null) {
			this.toastHelper = null;
		}
		
	}
	
	public void onRequestPermissionsResult(int _requestCode, String[] _permissions, int[] _grantResults) {
		super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults);
		if (_requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
			if (_grantResults.length > 0 && _grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				binding.buttonExtract.performClick();
			} else {
				// Permission denied.
				toastHelper.showToast("Permission denied. Cannot extract APK.");
			}
		}
		
	}
	public boolean _checkStoragePermission() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			// Android lower than 6.0, permission is granted at installation 
			return true;
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			return true;
		}
		
		// Android 6.0 until 10.0 (API 23 a 29)
		if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
			return false;
		}
		
	}
	
}
