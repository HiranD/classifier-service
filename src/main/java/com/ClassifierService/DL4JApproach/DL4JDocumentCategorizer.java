package com.ClassifierService.DL4JApproach;

import com.ClassifierService.DL4JApproach.tools.MeansBuilder;
import org.nd4j.linalg.primitives.Pair;
import com.ClassifierService.DL4JApproach.tools.LabelSeeker;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class DL4JDocumentCategorizer {
    ParagraphVectors paragraphVectors;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;

    final Logger log = LoggerFactory.getLogger(DL4JDocumentCategorizer.class);

    public DL4JDocumentCategorizer(ParagraphVectors paragraphVectors,LabelAwareIterator iterator, TokenizerFactory tokenizerFactory){
        this.paragraphVectors = paragraphVectors;
        this.iterator = iterator;
        this.tokenizerFactory = tokenizerFactory;
    }

    public void checkUnlabeledData(File unClassifiedResource) throws FileNotFoundException {

        FileLabelAwareIterator unClassifiedIterator = new FileLabelAwareIterator.Builder()
                .addSourceFolder(unClassifiedResource)
                .build();

        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>)paragraphVectors.getLookupTable(),
                tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        while (unClassifiedIterator.hasNextDocument()) {
            LabelledDocument document = unClassifiedIterator.nextDocument();
            INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
            List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

            log.info("Document '" + document.getLabels() + "' falls into the following categories: ");
            for (Pair<String, Double> score: scores) {
                log.info("        " + score.getFirst() + ": " + score.getSecond());
            }
        }
    }

    public void checkUnlabeledData(String content) throws FileNotFoundException {

        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>)paragraphVectors.getLookupTable(),
                tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

            INDArray documentAsCentroid = meansBuilder.stringAsVector(content);
            List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

            log.info("Content falls into the following categories: ");
            for (Pair<String, Double> score: scores) {
                log.info("        " + score.getFirst() + ": " + score.getSecond());
            }

    }
}