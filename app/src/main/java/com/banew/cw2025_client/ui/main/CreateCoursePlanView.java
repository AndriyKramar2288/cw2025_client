package com.banew.cw2025_client.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.banew.cw2025_client.GlobalApplication;
import com.banew.cw2025_client.data.DataSource;

import java.util.ArrayList;
import java.util.List;

public class CreateCoursePlanView extends ViewModel {

    private final DataSource dataSource;
    private final MutableLiveData<Form> form = new MutableLiveData<>(new Form());

    public CreateCoursePlanView() {
        dataSource = GlobalApplication.getInstance().getDataSource();
    }

    public LiveData<Form> getForm() {
        return form;
    }

    public void putValue(String name, String description) {
        Form localForm = form.getValue();
        if (localForm != null) {
            localForm.name = name;
            localForm.description = description;
        }
    }

    private static class Form {
        String name = "";
        String description = "";
        List<FormTopic> topics = new ArrayList<>();
    }

    private static class FormTopic {
        String name;
        String description;
    }
}
