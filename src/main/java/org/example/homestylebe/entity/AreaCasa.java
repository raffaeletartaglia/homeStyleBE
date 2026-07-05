package org.example.homestylebe.entity;

public enum AreaCasa {
    CUCINA,
    SOGGIORNO,
    SALOTTO,
    CAMERA_DA_LETTO,
    CAMERETTA,
    BAGNO,
    INGRESSO,
    CORRIDOIO_DISIMPEGNO,
    RIPOSTIGLIO_LAVANDERIA,
    STUDIO,
    TERRAZZO_BALCONE,
    GIARDINO_ESTERNO,
    GARAGE_BOX,
    CANTINA_TAVERNA,
    STANZA_COMUNE;

    public boolean isAreaCasa(AreaCasa areaCasa) {
        return areaCasa != null && areaCasa == this;
    }

}
