package org.jax.mgi.shr.ioutils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataCompare2
{
   private BufferedWriter writer = null;

   public DataCompare2(String name) throws IOException
   {
      writer =
         new BufferedWriter(new FileWriter(name));

   }


   public void createFile() throws IOException
   {
      writer.write("ID          Mm.1\n");
      writer.write("TITLE       S100 calcium binding protein A10 (calpactin)\n");
      writer.write("GENE        S100a10\n");
      writer.write("CYTOBAND    3 41.7 cM\n");
      writer.write("MGI         1339468\n");
      writer.write("LOCUSLINK   20194\n");
      writer.write("EXPRESS     embryo, whole embryo ;lymph ;liver ;adult ;hippocampus ;bowel ;head ;spleen ;mammary gland ;organ of corti ;colon ;tongue ;t cell ;pituitary gland ;branchial arches ;gonad ;pineal-glands ;nervous system ;embryo, ectoplacental cone ;heart ;mammary ;muscle ;brain ;kidney ;ovary and uterus ;germinal b-cell from resting spleen ;cerebellum ;lung ;stomach ;dissected endoderm ;embryo ;macrophage ;pool ;adipose ;small intestine ;pancreas ;embryonal carcinoma ;cecum ;testis ;sympathetic ganglion ;thymus ;intestine ;neural retina \n");
      writer.write("CHROMOSOME  3\n");
      writer.write("STS         ACC=M16465 NAME=M16465 UNISTS=178878\n");
      writer.write("PROTSIM     ORG=Homo sapiens; PROTGI=107251; PROTID=pir:JC1139; PCT=91; ALN=96\n");
      writer.write("PROTSIM     ORG=Mus musculus; PROTGI=116487; PROTID=sp:P08207; PCT=100; ALN=96\n");
      writer.write("PROTSIM     ORG=Rattus norvegicus; PROTGI=116489; PROTID=sp:P05943; PCT=94; ALN=93\n");
      writer.write("SCOUNT      184\n");
      writer.write("SEQUENCE-----------------------------------------------\n");
      writer.write("ID          Mm.5\n");
      writer.write("TITLE       homeo box A10\n");
      writer.write("GENE        Hoxa10\n");
      writer.write("CYTOBAND    6 26.33 cM\n");
      writer.write("MGI         96171\n");
      writer.write("LOCUSLINK   15395\n");
      writer.write("EXPRESS     kidney ;mullerian duct includes surrounding region ;oviduct ;bone marrow ;mammary gland ;embryonic body between diaphragm region and neck ;forelimb ;muscle ;embryo, whole embryo ;undifferentiated limb ;placenta and extra embryonic tissue ;colon ;ovary and uterus ;parthenogenote ;wolffian duct includes surrounding region ;whole body ;epididymis ;spinal cord ;vagina \n");
      writer.write("CHROMOSOME  6\n");
      writer.write("STS         ACC=- NAME=Hoxa10 UNISTS=143361\n");
      writer.write("PROTSIM     ORG=Caenorhabditis elegans; PROTGI=7510074; PROTID=pir:T31611; PCT=30; ALN=325\n");
      writer.write("PROTSIM     ORG=Drosophila melanogaster; PROTGI=1711344; PROTID=sp:P09077; PCT=28; ALN=359\n");
      writer.write("PROTSIM     ORG=Homo sapiens; PROTGI=13124743; PROTID=sp:P31260; PCT=89; ALN=398\n");
      writer.write("PROTSIM     ORG=Mus musculus; PROTGI=1708349; PROTID=sp:P31310; PCT=100; ALN=398\n");
      writer.write("PROTSIM     ORG=Rattus norvegicus; PROTGI=1708345; PROTID=sp:P52949; PCT=36; ALN=166\n");
      writer.write("SCOUNT      58\n");
      writer.write("SEQUENCE-----------------------------------------------\n");
      writer.close();
   }
}
