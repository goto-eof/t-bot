import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-yes-not-dialog',
  templateUrl: './yes-not-dialog.component.html',
  styleUrls: ['./yes-not-dialog.component.css']
})
export class YesNotDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<YesNotDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }
  onNoClick(): void {
    this.dialogRef.close(false);
  }

}
