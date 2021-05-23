package com.example.proje.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.proje.R;

public class AnasayfaFragment extends Fragment implements View.OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anasayfa, container, false);

        Button film = view.findViewById(R.id.film);
        Button dizi = view.findViewById(R.id.dizi);
        Button kitap = view.findViewById(R.id.kitap);

       film.setOnClickListener(this);
       dizi.setOnClickListener(this);
       kitap.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()){
            case R.id.film:
                fragment= new FilmFragment();
                replaceFragment(fragment);
                break;
            case R.id.dizi:
                fragment= new DiziFragment();
                replaceFragment(fragment);
                break;
            case R.id.kitap:
                fragment= new KitapFragment();
                replaceFragment(fragment);
                break;

        }

    }
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}