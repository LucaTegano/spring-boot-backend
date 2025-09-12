package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecentNotesDto {
    private List<NoteListItemDto> previous7Days;
    private List<NoteListItemDto> previous30Days;
    private List<NoteListItemDto> latestNotes;
}