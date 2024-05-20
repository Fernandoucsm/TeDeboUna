package com.example.tedebouna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

public class RegistrarHabilidades extends Fragment {

    private LinearLayout skillsLayout;
    private List<SkillField> skillFields;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registrar_habilidades, container, false);

        skillsLayout = view.findViewById(R.id.skillsLayout);
        skillFields = new ArrayList<>();

        Button addSkillButton = view.findViewById(R.id.addSkillButton);
        addSkillButton.setOnClickListener(v -> addSkillField(null, skillFields.size() + 1));

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveSkills());

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        loadSkills();

        return view;
    }

    private void addSkillField(@Nullable String skillText, int index) {
        View skillView = LayoutInflater.from(getContext()).inflate(R.layout.skill_item, skillsLayout, false);
        EditText skillEditText = skillView.findViewById(R.id.skillEditText);
        ImageButton deleteButton = skillView.findViewById(R.id.deleteButton);

        if (skillText != null) {
            skillEditText.setText(skillText);
        }

        deleteButton.setOnClickListener(v -> {
            skillsLayout.removeView(skillView);
            skillFields.removeIf(skillField -> skillField.index == index);

            // Obtén la referencia al documento del usuario
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userDoc = db.collection("users").document(userId);

            // Elimina la habilidad de Firebase usando el índice
            userDoc.update("skill" + index, FieldValue.delete())
                    .addOnSuccessListener(aVoid -> Log.d("Delete Skill", "Skill successfully deleted!"))
                    .addOnFailureListener(e -> Log.w("Delete Skill", "Error deleting skill", e));
        });

        skillsLayout.addView(skillView);
        skillFields.add(new SkillField(skillEditText, deleteButton, index));
    }

    private void saveSkills() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int i = 0; i < skillFields.size(); i++) {
            String skill = skillFields.get(i).skillEditText.getText().toString();
            if (skill.isEmpty()) {
                Toast.makeText(getContext(), "El campo de habilidad no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("users").document(userId)
                    .update("skill" + (i + 1), skill);
        }
        Toast.makeText(getContext(), "Habilidades guardadas exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void loadSkills() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int i = 1;
                    while (documentSnapshot.contains("skill" + i)) {
                        String skill = documentSnapshot.getString("skill" + i);
                        addSkillField(skill, i);
                        i++;
                    }
                })
                .addOnFailureListener(e -> Log.e("Skills Error", "Error loading skills", e));
    }

    private static class SkillField {
        EditText skillEditText;
        ImageButton deleteButton;
        int index;

        SkillField(EditText skillEditText, ImageButton deleteButton, int index) {
            this.skillEditText = skillEditText;
            this.deleteButton = deleteButton;
            this.index = index;
        }
    }
}
