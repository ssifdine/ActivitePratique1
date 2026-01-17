import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { BillDetail } from '../models/bill.model';

@Injectable({
  providedIn: 'root'
})
export class PdfGeneratorService {

  constructor() { }

  /**
   * Génère un PDF à partir de l'élément HTML de la facture
   */
  async generatePdfFromHtml(elementId: string, filename: string): Promise<void> {
    const element = document.getElementById(elementId);

    if (!element) {
      console.error('Element not found');
      return;
    }

    try {
      // Configuration pour une meilleure qualité
      const canvas = await html2canvas(element, {
        scale: 2,
        useCORS: true,
        logging: false,
        backgroundColor: '#f8fafc'
      });

      const imgData = canvas.toDataURL('image/png');

      // Format A4
      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4'
      });

      const imgWidth = 210; // Largeur A4 en mm
      const pageHeight = 297; // Hauteur A4 en mm
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      // Ajouter la première page
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      // Ajouter des pages supplémentaires si nécessaire
      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      pdf.save(filename);
    } catch (error) {
      console.error('Erreur lors de la génération du PDF:', error);
      throw error;
    }
  }

  /**
   * Génère un PDF en utilisant jsPDF directement (meilleure qualité et plus petit fichier)
   */
  generatePdfDirect(bill: BillDetail): void {
    // Validation des données
    if (!bill || !bill.customer || !bill.productItems || bill.productItems.length === 0) {
      console.error('Données de facture invalides');
      throw new Error('Impossible de générer le PDF : données manquantes');
    }
    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4'
    });

    const pageWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();
    const margin = 15;
    let yPosition = margin;

    // Couleurs
    const primaryColor: [number, number, number] = [124, 58, 237]; // Purple-600
    const darkColor: [number, number, number] = [31, 41, 55]; // Gray-800
    const lightColor: [number, number, number] = [107, 114, 128]; // Gray-500

    // En-tête avec fond violet
    pdf.setFillColor(...primaryColor);
    pdf.rect(0, 0, pageWidth, 45, 'F');

    // Logo et titre
    pdf.setTextColor(255, 255, 255);
    pdf.setFontSize(24);
    pdf.setFont('helvetica', 'bold');
    pdf.text('FACTURE', margin, 20);

    pdf.setFontSize(18);
    pdf.text(`#${bill.id}`, margin, 30);

    // Date et statut (alignés à droite)
    pdf.setFontSize(10);
    pdf.setFont('helvetica', 'normal');
    const dateText = new Date(bill.billingDate).toLocaleDateString('fr-FR');
    pdf.text('Date de facturation', pageWidth - margin - 50, 20);
    pdf.setFontSize(12);
    pdf.setFont('helvetica', 'bold');
    pdf.text(dateText, pageWidth - margin - 50, 27);

    // Badge "Payé"
    pdf.setFillColor(34, 197, 94); // Green-500
    pdf.roundedRect(pageWidth - margin - 30, 32, 25, 8, 3, 3, 'F');
    pdf.setFontSize(9);
    pdf.text('Payé', pageWidth - margin - 22, 37.5);

    yPosition = 60;

    // Section Client et Émetteur
    pdf.setTextColor(...darkColor);
    pdf.setFontSize(9);
    pdf.setFont('helvetica', 'bold');
    pdf.text('FACTURÉ À', margin, yPosition);
    pdf.text('DE', pageWidth / 2 + margin, yPosition);

    yPosition += 8;

    // Informations client
    pdf.setFillColor(249, 250, 251);
    pdf.roundedRect(margin, yPosition, (pageWidth / 2) - margin - 5, 35, 3, 3, 'F');

    pdf.setTextColor(...darkColor);
    pdf.setFontSize(12);
    pdf.setFont('helvetica', 'bold');
    pdf.text(bill.customer.fullName || 'N/A', margin + 5, yPosition + 7);

    pdf.setFontSize(9);
    pdf.setFont('helvetica', 'normal');
    pdf.setTextColor(...lightColor);
    pdf.text(bill.customer.email || 'N/A', margin + 5, yPosition + 13);
    if (bill.customer.phone) {
      pdf.text(bill.customer.phone, margin + 5, yPosition + 18);
    }
    if (bill.customer.street) {
      pdf.text(bill.customer.street, margin + 5, yPosition + 23);
    }
    if (bill.customer.city || bill.customer.country) {
      const location = `${bill.customer.city || ''}${bill.customer.city && bill.customer.country ? ', ' : ''}${bill.customer.country || ''}`;
      pdf.text(location, margin + 5, yPosition + 28);
    }

    // Informations émetteur
    pdf.setFillColor(243, 232, 255);
    pdf.roundedRect(pageWidth / 2 + margin, yPosition, (pageWidth / 2) - margin - 15, 35, 3, 3, 'F');

    pdf.setTextColor(...darkColor);
    pdf.setFontSize(12);
    pdf.setFont('helvetica', 'bold');
    pdf.text('App-MicroService', pageWidth / 2 + margin + 5, yPosition + 7);

    pdf.setFontSize(9);
    pdf.setFont('helvetica', 'normal');
    pdf.setTextColor(...lightColor);
    pdf.text('Your Company Name', pageWidth / 2 + margin + 5, yPosition + 13);
    pdf.text('contact@company.com', pageWidth / 2 + margin + 5, yPosition + 18);
    pdf.text('123 Business Street', pageWidth / 2 + margin + 5, yPosition + 23);
    pdf.text('City, Country 12345', pageWidth / 2 + margin + 5, yPosition + 28);

    yPosition += 45;

    // Tableau des articles
    pdf.setTextColor(...darkColor);
    pdf.setFontSize(9);
    pdf.setFont('helvetica', 'bold');
    pdf.text('ARTICLES DE LA FACTURE', margin, yPosition);

    yPosition += 8;

    // En-tête du tableau
    pdf.setFillColor(249, 250, 251);
    pdf.rect(margin, yPosition, pageWidth - 2 * margin, 10, 'F');

    pdf.setFontSize(8);
    pdf.setTextColor(...lightColor);
    pdf.text('PRODUIT', margin + 3, yPosition + 6);
    pdf.text('QTÉ', pageWidth - margin - 80, yPosition + 6);
    pdf.text('PRIX UNIT.', pageWidth - margin - 55, yPosition + 6);
    pdf.text('TOTAL', pageWidth - margin - 25, yPosition + 6);

    yPosition += 12;

    // Lignes du tableau
    pdf.setFont('helvetica', 'normal');
    pdf.setTextColor(...darkColor);

    bill.productItems.forEach((item, index) => {
      if (yPosition > pageHeight - 80) {
        pdf.addPage();
        yPosition = margin;
      }

      // Ligne de séparation
      if (index > 0) {
        pdf.setDrawColor(229, 231, 235);
        pdf.line(margin, yPosition - 2, pageWidth - margin, yPosition - 2);
      }

      pdf.setFontSize(10);
      pdf.setFont('helvetica', 'bold');
      pdf.text(item.product?.name || 'Produit inconnu', margin + 3, yPosition + 4);

      pdf.setFontSize(8);
      pdf.setFont('helvetica', 'normal');
      pdf.setTextColor(...lightColor);
      const description = item.product?.description
        ? (item.product.description.length > 50
          ? item.product.description.substring(0, 47) + '...'
          : item.product.description)
        : 'No description';
      pdf.text(description, margin + 3, yPosition + 9);

      pdf.setTextColor(...darkColor);
      pdf.text((item.quantity || 0).toString(), pageWidth - margin - 80, yPosition + 6);
      pdf.text(`${(item.product?.price || 0).toFixed(2)} MAD`, pageWidth - margin - 60, yPosition + 6);

      pdf.setFont('helvetica', 'bold');
      pdf.text(`${(item.totalPrice || 0).toFixed(2)} MAD`, pageWidth - margin - 35, yPosition + 6);

      yPosition += 15;
    });

    yPosition += 10;

    // Section des totaux
    const totalsX = pageWidth - margin - 80;

    // Sous-total
    pdf.setFont('helvetica', 'normal');
    pdf.setTextColor(...lightColor);
    pdf.text('Sous-total', totalsX, yPosition);
    pdf.setTextColor(...darkColor);
    pdf.setFont('helvetica', 'bold');
    pdf.text(`${(bill.subtotal || 0).toFixed(2)} MAD`, pageWidth - margin - 35, yPosition, { align: 'right' });

    yPosition += 8;

    // Taxe
    pdf.setFont('helvetica', 'normal');
    pdf.setTextColor(...lightColor);
    pdf.text('Taxe (20%)', totalsX, yPosition);
    pdf.setTextColor(...darkColor);
    pdf.setFont('helvetica', 'bold');
    pdf.text(`${(bill.tax || 0).toFixed(2)} MAD`, pageWidth - margin - 35, yPosition, { align: 'right' });

    yPosition += 12;

    // Total final avec fond
    pdf.setFillColor(243, 232, 255);
    pdf.roundedRect(totalsX - 5, yPosition - 8, 85, 18, 3, 3, 'F');

    pdf.setFontSize(10);
    pdf.setTextColor(...primaryColor);
    pdf.text('MONTANT TOTAL', totalsX, yPosition);

    pdf.setFontSize(16);
    pdf.setFont('helvetica', 'bold');
    pdf.text(`${(bill.totalAmount || 0).toFixed(2)} MAD`, pageWidth - margin - 35, yPosition + 6, { align: 'right' });

    yPosition += 25;

    // Note de paiement
    pdf.setFillColor(219, 234, 254);
    pdf.roundedRect(margin, yPosition, pageWidth - 2 * margin, 20, 3, 3, 'F');

    pdf.setFontSize(9);
    pdf.setTextColor(30, 64, 175);
    pdf.setFont('helvetica', 'bold');
    pdf.text('Information de paiement', margin + 5, yPosition + 7);

    pdf.setFont('helvetica', 'normal');
    pdf.text('Cette facture a été payée en totalité. Merci pour votre confiance !', margin + 5, yPosition + 13);

    yPosition += 28;

    // Notes
    pdf.setFontSize(9);
    pdf.setTextColor(...lightColor);
    pdf.setFont('helvetica', 'bold');
    pdf.text('NOTES', margin, yPosition);

    yPosition += 6;
    pdf.setFont('helvetica', 'normal');
    pdf.text('Merci pour votre confiance ! Pour toute question concernant cette facture,', margin, yPosition);
    pdf.text('veuillez nous contacter à support@company.com', margin, yPosition + 5);

    // Pied de page
    pdf.setFontSize(8);
    pdf.setTextColor(...lightColor);
    const footerText = `Facture générée le ${new Date(bill.billingDate).toLocaleString('fr-FR')} • App-MicroService`;
    pdf.text(footerText, pageWidth / 2, pageHeight - 10, { align: 'center' });

    // Sauvegarder le PDF
    pdf.save(`facture-${bill.id}.pdf`);
  }

  /**
   * Imprime la facture
   */
  printInvoice(): void {
    window.print();
  }
}
