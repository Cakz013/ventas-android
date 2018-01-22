package com.example.cesar.empresa;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.Barrio;
import empresa.dao.Cliente;
import empresa.dao.ClienteDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Localidad;
import empresa.dao.Region;
import empresa.dao.Rubro;
import empresa.dao.TipoCliente;
import empresa.dao.TipoDocumento;
import empresa.dao.TipoPersona;
import empresa.dao.Zona;
import tpoffline.CacheVarios;
import tpoffline.Config;
import tpoffline.ConfiguracionActividad;
import tpoffline.EnvioDatos;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.Forms;
import tpoffline.utils.UtilsAC;
import tpoffline.utils.Values;
import tpoffline.widget.Dialogos;
import tpoffline.widget.UIBuilder;

/**
 * Created by Cesar on 7/13/2017.
 */

public class FormNuevoCliente extends Activity {



    private TipoPersona tipoPersonaSelecta;
    private TipoDocumento tipoDocumentoSelecta;
    private Localidad localidadSelecta;
    private Region regionSelecta;
    private Barrio barrioClienteSelecto;
    private Zona zonaClienteSelecto;
    private Rubro rubroClienteSelecto;
    private TipoCliente tipoCliente;

    public void accionCancelarNuevoCliente(View view) {
        finish();
    }

    public void accionGuardarClienteNuevo(View view) {

        if(tipoCliente.getIdtipocliente().longValue() == TipoCliente.TIPO_CLIENTE_NORMAL) {
            guardarClienteNormalHelper();
        } else {
            guardarClienteProspectoHelper();
        }


    }

    private void guardarClienteProspectoHelper() {

        String nombresRazonSocial = getNombresRazonSocial().toUpperCase();
        String direccion = getDireccion().toUpperCase();

        String observacion = getObservacion().toUpperCase();

        String telefonos = getTelefonos().toUpperCase();
        String celular = getCel().toUpperCase();

        String ciudad = getCiudad().toUpperCase();

        if(nombresRazonSocial.length() < 4) {
            UtilsAC.showAceptarDialog("Ingrese la razon social", "Complete datos", this);
            return;
        }
        if(nombresRazonSocial.length() < 4) {
            UtilsAC.showAceptarDialog("Ingrese la ciudad del prospecto", "Complete datos", this);
            return;
        }
        if(direccion.length() < 4) {
            UtilsAC.showAceptarDialog("Ingrese la direccion", "Complete datos", this);
            return;
        }

		/*if(  ! ( telefonos.length()>=4  || celular.length() >= 4) ) {
			UtilsAC.showAceptarDialog("Ingrese un numero de telefono o celular", "Complete datos", this);
			return;
		}*/

        if(observacion.length() < 4) {
            UtilsAC.showAceptarDialog("Ingrese referencia/obs.", "Complete datos", this);
            return;
        }

        try {
            Cliente clienteProspectoNuevo = EnvioDatos.enviarClienteProspectoNuevo(this, nombresRazonSocial, ciudad , direccion, telefonos,celular,
                    rubroClienteSelecto.getIdrubro(), observacion, regionSelecta.getCodregion(),localidadSelecta.getIdlocalidad());

			/*Cliente clienteProspectoNuevo = EnvioDatos.enviarClienteProspectoNuevo(this, nombresRazonSocial, localidadSelecta.getIdlocalidad() , direccion, telefonos,celular,
					rubroClienteSelecto.getIdrubro(), observacion );*/

            Forms.enable(this, R.id.btnGuardarClienteNuevo, false);

            UtilsAC.showAceptarDialog("Se guardaron los datos del prospecto. El numero de CLIENTE PROSPECTO ES: "+
                    clienteProspectoNuevo.getIdcliente()  , "Guardado", this);

            UIBuilder.recorrerEditTexts((LinearLayout)findViewById(R.id.llClienteNuevo),
                    new AccionSeleccionItem<EditText>() {

                        @Override
                        public void ejecutarAccion(EditText et) {
                            if(et instanceof View) {
                                ((View)et).setEnabled(false);
                            }


                        }
                    } );

        } catch (Throwable e) {
            Dialogos.showErrorDialog("Error al intentar guardar. " + e.getMessage() , "Error", this, e);
        }


    }


    private void guardarClienteNormalHelper() {
        String nroDocRuc = getNroDocRuc().toUpperCase();
        String nombresRazonSocial = getNombresRazonSocial().toUpperCase();
        String apellidos = getApellidos().toUpperCase();
        String direccion = getDireccion().toUpperCase();
        String email = getEmail();
        String observacion = getObservacion().toUpperCase();

        String telefonos = getTelefonos().toUpperCase();
        String celular = getCel().toUpperCase();
        String nombreFantasia = getNombreFantasia().toUpperCase();

        boolean validoValores = avisarValidoString(nroDocRuc, 4,
                "Nro. Doc./RUC");
        validoValores = validoValores
                && avisarValidoString(nombresRazonSocial, 3, "nombres");

        validoValores = validoValores
                && avisarValidoString(direccion, 3, "direccion");
        validoValores = validoValores && avisarValidoEmail(email);
        validoValores = validoValores
                && avisarValidoObjeto(tipoPersonaSelecta,
                "Selecione antes el tipo de persona");
        validoValores = validoValores
                && avisarValidoObjeto(regionSelecta,
                "selecione antes region del cliente");
        validoValores = validoValores
                && avisarValidoObjeto(localidadSelecta,
                "selecione antes la localidad del cliente");

        validoValores = validoValores
                && avisarValidoObjeto(tipoDocumentoSelecta,
                "selecione antes el tipo de documento");
        validoValores = validoValores
                && avisarValidoObjeto(zonaClienteSelecto,
                "selecione antes la zona de cliente");
        validoValores = validoValores
                && avisarValidoObjeto(rubroClienteSelecto,
                "selecione antes el rubro del cliente");

        barrioClienteSelecto = (Barrio) Values.nullToVal(
                Barrio.NINGUNO_DESCONOCIDO, barrioClienteSelecto);

        if (!validoValores)
            return;

        try {

            Cliente cn = EnvioDatos.EnviarClienteNuevo(telefonos, celular,
                    nroDocRuc, nombresRazonSocial, apellidos, direccion, email,
                    observacion, nombreFantasia, tipoPersonaSelecta, tipoDocumentoSelecta,
                    regionSelecta, localidadSelecta,
                    zonaClienteSelecto, rubroClienteSelecto,
                    barrioClienteSelecto);

            cn = new Cliente(cn.getIdcliente(), cn.getIdpersona(), nombresRazonSocial, apellidos, direccion, telefonos, nroDocRuc,
                    localidadSelecta.getDescripcion(), tipoDocumentoSelecta.getCodtipodocumento(),
                    celular, "NORMAL", nombreFantasia, "", zonaClienteSelecto.getDescripcion(),
                    0.0, email, barrioClienteSelecto.getDescripcion(), rubroClienteSelecto.getDescripcion(),
                    regionSelecta.getDescripcion(), observacion, 1, true);


            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                    Config.SQLITE_DB_NAME, null);

            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            ClienteDao objDao = daoSession.getClienteDao();

            objDao.insert(cn);

            db.close();

            UtilsAC.showAceptarDialog("Listo. "
                    + "El numero de cliente nuevo es: "
                    + cn.getIdcliente() + " - No es necesario que actualice el stock. Puede directamente ya tomar un pedido con el cliente nuevo", "Guardado", this);

            CacheVarios.resetCache();

            findViewById(R.id.btnGuardarClienteNuevo).setEnabled(false);

        } catch (Exception e) {
            UtilsAC.showAceptarDialog(
                    "Error al guardar cliente nuevo: " + e.getMessage(), "Error",
                    this);

            e.printStackTrace();
        }

    }

    private String getNombreFantasia() {
        return Forms.getInText(this, R.id.tfNombreFantasia).trim();
    }

    private String getCel() {
        return Forms.getInText(this, R.id.tfCelularCliente).trim();
    }

    private String getTelefonos() {
        return Forms.getInText(this, R.id.tfTelefonosCliente).trim();
    }

    private boolean avisarValidoObjeto(Object o, String mensaje) {
        if (o == null) {
            UtilsAC.showAceptarDialog(mensaje, "Selecione", this);
            return false;
        } else {
            return true;
        }
    }

    private boolean avisarValidoEmail(String email) {
        boolean r = false;

        if (email == null || email.length() == 0)
            return true;

        if (email.length() > 4) {
            r = !TextUtils.isEmpty(email)
                    && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches();
        }

        if (!r) {
            UtilsAC.showAceptarDialog("Direccion de email no valida: " + email,
                    "Email no valido", this);
        }

        return r;
    }

    private boolean avisarValidoString(String s, int longitudMinima,
                                       String valueName) {
        boolean r = false;
        if (s == null) {
            r = false;
            UtilsAC.showAceptarDialog("Error El valor de: " + valueName
                    + " es nulo", "Error null", this);
        } else if (s.length() >= longitudMinima) {
            r = true;
        } else {
            r = false;
            UtilsAC.showAceptarDialog("El valor de: " + valueName
                    + " no es valido", "Invalido/a " + valueName, this);
        }
        return r;
    }

    private String getObservacion() {
        return Forms.getInText(this, R.id.tfObservacionCliente).trim();
    }

    private String getCiudad() {
        return Forms.getInText(this, R.id.tfCiudadProspecto).trim();
    }


    private String getEmail() {
        return Forms.getInText(this, R.id.tfEmailCliente).trim();
    }

    private String getDireccion() {
        return Forms.getInText(this, R.id.tfDireccionCliente).trim();
    }

    private String getApellidos() {
        return Forms.getInText(this, R.id.tfApellidosCliente).trim();
    }

    private String getNombresRazonSocial() {
        return Forms.getInText(this, R.id.tfNombreRazonSocialCliente).trim();
    }

    private String getNroDocRuc() {
        return Forms.getInText(this, R.id.tfNroDocRucCliente).trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_nuevo_cliente);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_nuevo_cliente, menu);

//        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);

        inicializarTipoPersona();
        inicializarTipoDocumento();

        inicializarRegiones();
        inicializarZonas();
        inicializarListaRubros();

        incializarListoTipoClientes();

       UIBuilder.editTextAllCaps((LinearLayout)findViewById(R.id.llClienteNuevo));


    }

    private void incializarListoTipoClientes() {

        List<TipoCliente> listaTipoClientes = new ArrayList<TipoCliente>();
        listaTipoClientes.add(new TipoCliente(TipoCliente.TIPO_CLIENTE_NORMAL, "CLIENTE NORMAL"));
        listaTipoClientes.add(new TipoCliente( TipoCliente.TIPO_CLIENTE_PROSPECTO, "PROSPECTO"));

        UIBuilder.newDropDownList(this, R.id.spTipoClienteNuevo,
                listaTipoClientes,
                new AccionSeleccionItem<TipoCliente>() {
                    @Override
                    public void ejecutarAccion(TipoCliente eleSelecto) {
                        seleccionarTipoCliente(eleSelecto);
                    }
                });

    }

    protected void seleccionarTipoCliente(TipoCliente eleSelecto) {
        tipoCliente = eleSelecto;
        if(tipoCliente.getIdtipocliente().longValue() == TipoCliente.TIPO_CLIENTE_PROSPECTO) {
            modoClienteNormal(false);
        } else {
            modoClienteNormal(true);
        }

    }

    private void modoClienteNormal(boolean esModoClienteNormal ) {

        Forms.visible(this, R.id.llClienteNormalD1, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD2, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD3, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD4, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD5, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD6, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD7, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD8, esModoClienteNormal);
        Forms.visible(this, R.id.llClienteNormalD9, esModoClienteNormal);

        if(! esModoClienteNormal) {
            //linea comentada para que se muestre localidad y region en tipo cliente prospecto
			/*Forms.visible(this, R.id.llClienteProspCiudad, true);*/
            Forms.visible(this, R.id.llClienteProspCiudad, false);
            Forms.visible(this, R.id.llClienteNormalD5, true);
            Forms.visible(this, R.id.llClienteNormalD6, true);


        } else {
            Forms.visible(this, R.id.llClienteProspCiudad, false);
        }


    }



    private void inicializarListaLocalidades( long idregion) {

        UIBuilder.newDropDownList(this, R.id.spLocalidades, Dao.getListaLocalidades((Context)this, idregion),
                new AccionSeleccionItem<Localidad>() {
                    @Override
                    public void ejecutarAccion(Localidad eleSelecto) {
                        seleccionarLocalidad(eleSelecto);
                    }
                });

    }

    protected void seleccionarLocalidad(Localidad lc) {
        this.localidadSelecta = lc;

        inicializarListaBarrios(lc.getIdlocalidad());

    }

    private void inicializarListaBarrios(Long idlocalidad) {

        Spinner sp = UIBuilder.newDropDownList(this, R.id.spBarrios, Dao.getListaBarrios(this, idlocalidad),
                new AccionSeleccionItem<Barrio>() {
                    @Override
                    public void ejecutarAccion(Barrio eleSelecto) {
                        seleccionarBarrio(eleSelecto);
                    }
                });

    }

    protected void seleccionarBarrio(Barrio b) {
        this.barrioClienteSelecto = b;

    }

    private void inicializarRegiones() {

        Spinner sp = UIBuilder.newDropDownList(this, R.id.spRegion, Dao.getListaRegiones(this),
                new AccionSeleccionItem<Region>() {
                    @Override
                    public void ejecutarAccion(Region eleSelecto) {
                        seleccionarRegion(eleSelecto);
                    }
                });


    }

    protected void seleccionarRegion(Region r) {
        this.regionSelecta = r;
        inicializarListaLocalidades(r.getCodregion());

    }

    private void inicializarTipoDocumento() {
        Spinner sp = UIBuilder.newDropDownList(this, R.id.spTipoDocumento, Dao.getListaTipoDocumentos(this),
                new AccionSeleccionItem<TipoDocumento>() {
                    @Override
                    public void ejecutarAccion(TipoDocumento eleSelecto) {
                        accionSeleccionarTipoDocumento(eleSelecto);
                    }
                });

    }

    private void inicializarZonas() {

        Spinner sp = UIBuilder.newDropDownList(this, R.id.spZonaCliente, Dao.getListaZonas((Context)this),
                new AccionSeleccionItem<Zona>() {
                    @Override
                    public void ejecutarAccion(Zona eleSelecto) {
                        accionSeleccionarZonaCliente(eleSelecto);
                    }
                });

    }

    private void inicializarListaRubros() {

        Spinner sp = UIBuilder.newDropDownList(this, R.id.spRubro, Dao.getListaRubro(this),
                new AccionSeleccionItem<Rubro>() {
                    @Override
                    public void ejecutarAccion(Rubro eleSelecto) {
                        seleccionarRubro(eleSelecto);
                    }
                });
    }






    protected void seleccionarRubro(Rubro element) {
        this.rubroClienteSelecto = element;

    }

    protected void accionSeleccionarRubroCliente(Spinner spinner) {
        this.rubroClienteSelecto = (Rubro) spinner.getSelectedItem();

    }

    protected void accionSeleccionarZonaCliente(Zona  z) {
        this.zonaClienteSelecto = z;

    }

    protected void accionSeleccionarTipoDocumento(TipoDocumento td) {
        this.tipoDocumentoSelecta = td;

    }

    private void inicializarTipoPersona() {

        Spinner sp = UIBuilder.newDropDownList(this, R.id.spTipoPersona, Dao.getListaTipoPersonas(this),
                new AccionSeleccionItem<TipoPersona>() {
                    @Override
                    public void ejecutarAccion(TipoPersona eleSelecto) {
                        accionSeleccionarTipoPersona(eleSelecto);
                    }
                });
    }

    protected void accionSeleccionarTipoPersona(TipoPersona tp) {
        this.tipoPersonaSelecta = tp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_form_nuevo_cliente, container, false);
            return rootView;
        }
    }

}
