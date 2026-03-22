package com.controlefinaneiro.api.transacao.service;


import com.controlefinaneiro.api.infra.notificacoes.eventos.RelatorioSolicitadoEvent;
import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private AuthService authService;

    public byte[] gerarRelatorioCompleto(List<TransacaoResponse> transacao, int mes, int ano){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);

        try{
            PdfWriter.getInstance(documento, out);
            documento.open();


            //Cabeçalho
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            documento.add(new Paragraph("RELATÓRIO FINANCEIRO DETALHADO", fontTitulo));
            documento.add(new Paragraph("Período de referência: " + mes + "/" + ano));
            documento.add(new Paragraph(" "));

            //Tabela de transações
            PdfPTable tabela = new PdfPTable(6);
            tabela.setWidthPercentage(100);

            //Cabeçalho da tabela
            tabela.addCell(new PdfPCell(new Phrase("Data")));
            tabela.addCell(new PdfPCell(new Phrase("Tipo de transação")));
            tabela.addCell(new PdfPCell(new Phrase("Descrição")));
            tabela.addCell(new PdfPCell(new Phrase("Categoria")));
            tabela.addCell(new PdfPCell(new Phrase("Método de Pagamento")));
            tabela.addCell(new PdfPCell(new Phrase("Valor")));

            BigDecimal totalReceitas = BigDecimal.ZERO;
            BigDecimal totalDespesas = BigDecimal.ZERO;


            //Listagem
            for (TransacaoResponse transacaoDTO : transacao) {
                tabela.addCell(transacaoDTO.dataTransacao().toString());
                tabela.addCell(transacaoDTO.tipo().toString());
                tabela.addCell(transacaoDTO.descricao());
                tabela.addCell(transacaoDTO.categoria().toString());
                tabela.addCell(transacaoDTO.metodoPagamento().toString());

                String valorFormatado = "R$ " + transacaoDTO.valor();
                tabela.addCell(valorFormatado);

                //Somatorio
                if(transacaoDTO.tipo().toString().equals("RECEITA")){
                    totalReceitas = totalReceitas.add(transacaoDTO.valor());
                }else{
                    totalDespesas = totalDespesas.add(transacaoDTO.valor());
                }
            }

            documento.add(tabela);


            //Consolidando
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("--- RESUMO CONSOLIDADO ---", fontTitulo));
            documento.add(new Paragraph("Total de receitas:  R$ " + totalReceitas));
            documento.add(new Paragraph("Total de despesas:  R$ " + totalDespesas));


            //Calculo balanço
            BigDecimal balanco = totalReceitas.subtract(totalDespesas);
            Font fontBalanco = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph pBalanco = new Paragraph("BALANÇO LÍQUIDO: R$ " + balanco, fontBalanco);

            documento.add(pBalanco);

            documento.close();
        }catch(Exception e){
            throw new RuntimeException("Erro ao processar PDF: " + e.getMessage());
        }

        return out.toByteArray();
    }

    public void solicitarRelatorioEmail(int mes,int ano) {
        Usuario usuario = authService.getUsuarioAutenticado();

        // Apenas dispara o evento e responde ao usuário imediatamente
        publisher.publishEvent(new RelatorioSolicitadoEvent(usuario, mes, ano));
    }
}
