package org.jax.mgi.shr.ioutils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataCompare2c
{
   private BufferedWriter writer = null;

   public DataCompare2c(String name) throws IOException
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
      writer.write("SEQUENCE    ACC=M16465; NID=g192360; PID=g309134\n");
      writer.write("SEQUENCE    ACC=AV164632; NID=g5430608; CLONE=3110027H11; LID=226\n");
      writer.write("//\n");
      writer.write("-----------------------------------------------\n");
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
      writer.write("SEQUENCE    ACC=AK002670; NID=g12832825\n");
      writer.write("SEQUENCE    ACC=L08757; NID=g825647; PID=g567213\n");
      writer.write("SEQUENCE    ACC=NM_008263; NID=g6680242; PID=g6680243\n");
      writer.write("SEQUENCE    ACC=BB755721; NID=g16202212; CLONE=G270063O14; END=3'; LID=563\n");
      writer.write("SEQUENCE    ACC=BB788542; NID=g16957038; CLONE=G430123F17; END=3'; LID=575\n");
      writer.write("SEQUENCE    ACC=AV144774; NID=g5348907; CLONE=2810439F03; END=3'; LID=224\n");
      writer.write("SEQUENCE    ACC=BB774107; NID=g16934787; CLONE=G430008H18; END=3'; LID=581\n");
      writer.write("SEQUENCE    ACC=BB731364; NID=g16114639; CLONE=E970007E22; END=3'; LID=566\n");
      writer.write("SEQUENCE    ACC=BB789618; NID=g16958114; CLONE=G430128H24; END=3'; LID=575\n");
      writer.write("SEQUENCE    ACC=BB504976; NID=g9493770; CLONE=D630050N11; END=3'; LID=431\n");
      writer.write("SEQUENCE    ACC=BB035597; NID=g8441983; CLONE=5930404L16; END=3'; LID=297\n");
      writer.write("SEQUENCE    ACC=BB786670; NID=g16955166; CLONE=G430112N22; END=3'; LID=584\n");
      writer.write("SEQUENCE    ACC=BB751840; NID=g16156076; CLONE=G270005N15; END=3'; LID=563\n");
      writer.write("SEQUENCE    ACC=BB440128; NID=g9282740; CLONE=D030020G03; END=3'; LID=425\n");
      writer.write("SEQUENCE    ACC=AV004434; NID=g4781284; CLONE=0610042C10; END=3'; LID=203\n");
      writer.write("SEQUENCE    ACC=BB094778; NID=g8739812; CLONE=9430048F23; END=3'; LID=395\n");
      writer.write("SEQUENCE    ACC=BB690485; NID=g16017218; CLONE=6820448F21; END=3'; LID=314\n");
      writer.write("SEQUENCE    ACC=BB781367; NID=g16942067; CLONE=G430072P22; END=3'; LID=574\n");
      writer.write("SEQUENCE    ACC=AV144796; NID=g5348929; CLONE=2810439G16; END=3'; LID=224\n");
      writer.write("SEQUENCE    ACC=AI648222; NID=g4726900; CLONE=IMAGE:1971478; END=3'; LID=136\n");
      writer.write("SEQUENCE    ACC=BB551477; NID=g9637843; CLONE=E230028H21; END=3'; LID=437\n");
      writer.write("SEQUENCE    ACC=AW319726; NID=g6749270; CLONE=IMAGE:2373115; END=3'; LID=136\n");
      writer.write("SEQUENCE    ACC=AV003271; NID=g4780121; CLONE=0610025N02; END=3'; LID=203\n");
      writer.write("SEQUENCE    ACC=BB496452; NID=g9469343; CLONE=D630003F17; END=3'; LID=431\n");
      writer.write("SEQUENCE    ACC=AV013744; NID=g4790736; CLONE=1110049N17; END=3'; LID=193\n");
      writer.write("SEQUENCE    ACC=AW990221; NID=g8185806; CLONE=IMAGE:1513370; END=5'; LID=389\n");
      writer.write("SEQUENCE    ACC=BB689939; NID=g16016672; CLONE=6820445I18; END=3'; LID=314\n");
      writer.write("-----------------------------------------------\n");
      writer.close();
   }
}
