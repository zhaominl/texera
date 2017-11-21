import { Component, OnInit } from '@angular/core';

declare var $: JQueryStatic;

import * as _ from 'lodash';
import * as backbone from 'backbone';
import * as joint from 'jointjs';

import { WorkflowDataService } from '../../service/current-workflow/workflow-data.service';
import { WorkflowUIService } from '../../service/current-workflow/workflow-ui.service';
import { OperatorDragDropService } from '../../service/operator-drag-drop/operator-drag-drop.service';

@Component({
  selector: 'texera-workflow-editor',
  templateUrl: './workflow-editor.component.html',
  styleUrls: ['./workflow-editor.component.scss']
})
export class WorkflowEditorComponent implements OnInit {

  private paper: joint.dia.Paper;

  constructor(private workflowDataService: WorkflowDataService,
    private workflowUIService: WorkflowUIService, private operatorDragDropService: OperatorDragDropService) {

  }

  ngOnInit() {
    this.paper = new joint.dia.Paper({
      el: $('#workflow-holder'),
      width: 600,
      height: 200,
      model: this.workflowDataService.workflowUI,
      gridSize: 1,
      snapLinks: true,
      linkPinning: false,
      validateConnection: function (sourceView, sourceMagnet, targetView, targetMagnet) {
        return sourceMagnet !== targetMagnet;
      }
    });

    jQuery('#workflow-holder').droppable({
      drop: (event, ui) => this.operatorDragDropService.onOperatorDrop(event, ui)
    });

    this.workflowDataService.registerWorkflowPaper(this.paper);

    this.paper.on('cell:pointerdown', (cellView, evt, x, y) => this.workflowUIService.selectOperator(cellView.model.id));
  }



}