import { Component, OnInit } from '@angular/core';
import { BillDetail } from '../../../core/models/bill.model';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BillService } from '../../../core/services/bill.service';
import { CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import {PdfGeneratorService} from '../../../core/services/pdf-generator-service';

@Component({
  selector: 'app-bill-details',
  imports: [
    CurrencyPipe,
    DatePipe,
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './bill-details.html',
  styleUrl: './bill-details.css',
})
export class BillDetails implements OnInit {

  bill!: BillDetail;
  isGeneratingPdf = false;

  constructor(
    private route: ActivatedRoute,
    private billService: BillService,
    private pdfGenerator: PdfGeneratorService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.billService.getById(id).subscribe(res => {
      this.bill = res.data;
    });
  }

  /**
   * Génère le PDF en utilisant html2canvas (capture l'apparence exacte)
   */
  async downloadPdfFromHtml(): Promise<void> {
    if (this.isGeneratingPdf) return;

    this.isGeneratingPdf = true;
    try {
      await this.pdfGenerator.generatePdfFromHtml(
        'invoice-content',
        `facture-${this.bill.id}.pdf`
      );
    } catch (error) {
      console.error('Erreur lors de la génération du PDF:', error);
      alert('Une erreur est survenue lors de la génération du PDF');
    } finally {
      this.isGeneratingPdf = false;
    }
  }

  /**
   * Génère le PDF directement avec jsPDF (meilleure qualité, fichier plus petit)
   */
  downloadPdfDirect(): void {
    if (this.isGeneratingPdf || !this.bill) return;

    this.isGeneratingPdf = true;
    try {
      this.pdfGenerator.generatePdfDirect(this.bill);
    } catch (error) {
      console.error('Erreur lors de la génération du PDF:', error);
      alert('Une erreur est survenue lors de la génération du PDF');
    } finally {
      this.isGeneratingPdf = false;
    }
  }

  /**
   * Imprime la facture
   */
  printInvoice(): void {
    this.pdfGenerator.printInvoice();
  }
}
