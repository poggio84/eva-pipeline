package uk.ac.ebi.eva.test.data;

/**
 * CSQ field generated by VEP. See 'VCF output' in http://www.ensembl.org/info/docs/tools/vep/vep_formats.html
 */
public class VariantToVariantAnnotationProcessorTestData {

    /**
     * Will be represented in Mongo like this:
     * {
     * "_id" : "1_1_A_T",
     * "annot" : {
     *  "ct" : [
     *      {
     *          "ensg" : "EPlOSAG00000001824",
     *          "enst" : "EPlOSAT00000003212",
     *          "strand" : "-1",
     *          "bt" : "ncRNA",
     *          "so" : [
     *              1631
     *          ]
     *      }
     *  ],
     *  "xrefs" : [
     *      {
     *          "id" : "EPlOSAT00000003212",
     *          "src" : "ensemblTranscript"
     *      },
     *      {
     *          "id" : "EPlOSAG00000001824",
     *          "src" : "ensemblGene"
     *      }
     *  ]
     *  }
     * }
     */
    public final static String SINGLE_PARTIAL_CSQ = "T|upstream_gene_variant|MODIFIER||EPlOSAG00000001824|Transcript|EPlOSAT00000003212|ncRNA|||||||||||3715|-1|||";

    /**
     * Will be represented in Mongo like this:
     * {
     * "_id" : "1_1_A_T",
     * "annot" : {
     *  "ct" : [
     *      {
     *          "ensg" : "gene",
     *          "enst" : "feature",
     *          "codon" : "codons",
     *          "strand" : "strand",
     *          "bt" : "biotype",
     *          "so" : [
     *              1631
     *          ]
     *      }
     *  ],
     *  "xrefs" : [
     *      {
     *          "id" : "gene",
     *          "src" : "ensemblGene"
     *      },
     *      {
     *          "id" : "feature",
     *          "src" : "ensemblTranscript"
     *      }
     *  ]
     *  }
     * }
     */
    public final static String SINGLE_FULL_CSQ = "allele|upstream_gene_variant|impact|symbol|gene|featureType|feature|biotype|exon|intron|hgvsC|hgvsP|0|0|proteinPosition|aminoAcids|codons|existingVariation|distance|strand|flags|symbolSource|hgncIs";

    public final static String MULTIPLE_CSQ = "T|upstream_gene_variant|MODIFIER||EPlOSAG00000001824|Transcript|EPlOSAT00000003212|ncRNA|||||||||||3715|-1|||,T|upstream_gene_variant|MODIFIER||EPlOSAG00000037140|Transcript|EPlOSAT00000038528|ncRNA|||||||||||3484|-1|||,T|downstream_gene_variant|MODIFIER||EPlOSAG00000037355|Transcript|EPlOSAT00000038743|ncRNA|||||||||||3484|1|||,T|upstream_gene_variant|MODIFIER||EPlOSAG00000038600|Transcript|EPlOSAT00000039988|ncRNA|||||||||||3693|-1|||,T|upstream_gene_variant|MODIFIER||EPlOSAG00000048367|Transcript|EPlOSAT00000049755|ncRNA|||||||||||3525|-1|||,T|downstream_gene_variant|MODIFIER||EPlOSAG00000049773|Transcript|EPlOSAT00000051161|ncRNA|||||||||||3714|1|||,T|upstream_gene_variant|MODIFIER||EPlOSAG00000049783|Transcript|EPlOSAT00000051171|ncRNA|||||||||||3714|-1|||,T|upstream_gene_variant|MODIFIER||EPlOSAG00000050457|Transcript|EPlOSAT00000051845|ncRNA|||||||||||387|1|||,T|upstream_gene_variant|MODIFIER||OS01G0113200|Transcript|OS01T0113200-01|protein_coding|||||||||||844|-1|||,T|synonymous_variant|LOW||OS01G0113300|Transcript|OS01T0113300-01|protein_coding|1/3||||469|418|140|L|Cta/Tta|||1|||,T|3_prime_UTR_variant|MODIFIER||OS01G0113325|Transcript|OS01T0113325-00|protein_coding|2/2||||1661|||||||-1|||,T|upstream_gene_variant|MODIFIER||OS01G0113350|Transcript|OS01T0113350-00|protein_coding|||||||||||4807|1|||";

    public final static String CSQ_WITH_AMPERSANDS = "A|splice_region_variant&synonymous_variant|LOW||OS01G0111200|Transcript|OS01T0111200-01|protein_coding|4/8||||448|390|130|S|tcG/tcA|||1|||";

}