import {Injectable} from '@angular/core';
import {YesNotDialogComponent} from '../modules/control-panel/control-panel-home/dialogs/yes-not-dialog/yes-not-dialog.component';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {
  }


  openYesNoDialog(title: string, message: string): MatDialogRef<YesNotDialogComponent> {
    return this.dialog.open(YesNotDialogComponent, {
      width: '250px',
      data: {title, message}
    });
  }
}
