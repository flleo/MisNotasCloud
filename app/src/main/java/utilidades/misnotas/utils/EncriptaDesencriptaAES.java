package utilidades.misnotas.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import utilidades.misnotas.model.Nota;

public class EncriptaDesencriptaAES {

    AESUtil aesUtil = new AESUtil();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Nota encriptacionAES(Nota nota) {
        Nota nota1 = null;
        String titulo = new String(aesUtil.encrypt(nota.getTitulo()));
        String contenido = new String(aesUtil.encrypt(nota.getContenido()));
        nota1 = new Nota(nota.getId(),nota.getUser_id(),titulo,contenido);
        return nota1;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Nota desencriptaAES(Nota nota)  {
        Nota nota1 = null;
        String titulo = aesUtil.decrypt(nota.getTitulo());
        String contenido = aesUtil.decrypt(nota.getContenido());
        nota1 = new Nota(nota.getId(),nota.getUser_id(),titulo,contenido);
        return nota1;

    }

}
