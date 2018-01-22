package tpoffline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import empresa.dao.Cliente;
import empresa.dao.Empresa;
import empresa.dao.Producto;
import empresa.dao.ProgramaVisita;
import empresa.dao.TipoClienteLog;
import tpoffline.CacheVarios;
import tpoffline.NullObject;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.*;
import tpoffline.utils.UIBuilder;

/**
 * Created by Cesar on 6/30/2017.
 */

public class DialogoProgramacionVisitaCambioEstado {


    private static final String ESTADO_VISITA_NO_EXITO = "VISITA NO EXITOSA";
    private Context context;
    //linea Agregada
    //private Context contextArt;

    private ProgramaVisita progVisita;

    private AlertDialog dialogo;
    protected String estadoVisitaGlobal;
    protected TipoClienteLog tipoClienteLogNoExito;
    private ArrayAdapter<ProgramaVisita> adapterProgramaVisita;

    private List<ProgramaVisita> listaProgrmaVisitas;

    private Activity formPadre;
    private AccionSimple accionAlAceptarDialogo;
    private Producto ArticuloSelecto;

    public DialogoProgramacionVisitaCambioEstado(Context context,
                                                 ProgramaVisita programaVisitaOperar,
                                                 ArrayAdapter<ProgramaVisita> adapterProgramaVisita, List<ProgramaVisita> listaProgrmaVisitas, AccionSimple accionAlAceptarDialogo) {

        if(listaProgrmaVisitas == null )
            throw new NullPointerException("listaProgrmaVisitas es null");

        if(adapterProgramaVisita == null )
            throw new NullPointerException("adapterProgramaVisita es null");

        this.context = context;
        this.formPadre = (Activity)context;
        this.progVisita = programaVisitaOperar;
        this.adapterProgramaVisita = adapterProgramaVisita;

        this.listaProgrmaVisitas = listaProgrmaVisitas;

        this. accionAlAceptarDialogo  =accionAlAceptarDialogo;



    }

    public void mostrarDialogoCambioEstado() {

        if(progVisita.esPrototipoNuevo()) {
            buildUIcasoNuevaVisita();
        } else {
            buildUICasoCambiarEstadoNoExitoso();
        }


    }

    private void buildUICasoCambiarEstadoNoExitoso() {
        if(progVisita.getIdregistroclientelog() == null)
            throw new IllegalStateException("Error registro no tiene id");

        if(progVisita.getIdventacab() != null)
            throw new IllegalStateException("Error la visita (" +  progVisita.getIdregistroclientelog()
                    +") ya fue marcada como exitosa");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Visita no exitosa");

        LayoutInflater li = LayoutInflater.from(context);
        final View vista = li.inflate(R.layout.layout_dialogo_cambio_estado_programa_visita, null);

        Forms.visible(vista, R.id.lvClienteVisitar, false);
        Forms.visible(vista, R.id.lvArticuloVisitar, false);


        TextView tvCliente = (TextView) vista.findViewById(R.id.tvCliente);
        //linea Agregada
        TextView tvArticulo = (TextView) vista.findViewById(R.id.tvArticulo);



        final EditText tvFechaVisita = (EditText) vista.findViewById(R.id.tvFechaVisita);

        tvCliente.setText(Dao.getClienteById(context, progVisita.getIdcliente()).toString());
        //linea Agregada
        tvArticulo.setText(Dao.getProductoById(context, progVisita.getIdproducto()).toString());


        //tvFechaVisita.setText(UtilsAC.formatFechaSimple(UtilsAC.makeSqlDateFromCurrentDate()));
        final Contenedor<Date>  contFechaReprog = new Contenedor<Date>();

        final Contenedor< TipoClienteLog> contTipoCl = new Contenedor<>();

        setAccionBotonReprogramar(vista, "Reprogramar", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tpoffline.widget.UIBuilder.showSelectorFecha(context, new AccionSeleccionItem<Date>() {
                    @Override
                    public void ejecutarAccion(Date eleSelecto) {
                        contFechaReprog.setValue(eleSelecto);

                        boolean fechaOK = activarAceptarSoloConvaloresCambioEstadoReprogramacion(dialogo, contTipoCl.getValue(), contFechaReprog);
                        if(fechaOK)
                            tvFechaVisita.setText( ""  + eleSelecto.toString());
                        else
                            tvFechaVisita.setText("ingresa una fecha valida");

                    }

                });
            }
        });

        Forms.visible(vista, R.id.tfBuscarClienteVisitar, false);
        Forms.visible(vista, R.id.tfBuscarArticuloVisitar, false);



        List<TipoClienteLog> listaTipoCliLog = new ArrayList<>();
        listaTipoCliLog.add(NullObject.NULL_TIPO_CLIENTE_LOG);
        listaTipoCliLog.addAll(Dao.getListaTipoClienteLog(context));

        listaTipoCliLog.remove(Dao.getRoDaoSession().getTipoClienteLogDao().load(TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA));

        Forms.enable(vista, R.id.spEstadoVisitaCliente, false);

        tpoffline.utils.UIBuilder.newDropDownList(vista, R.id.spMotivosVisitaNoExitosa, listaTipoCliLog, new AccionSeleccionItem<TipoClienteLog>() {
            @Override
            public void ejecutarAccion(TipoClienteLog tipoClienteLog) {
                contTipoCl.setValue(tipoClienteLog);

                activarAceptarSoloConvaloresCambioEstadoReprogramacion(dialogo, tipoClienteLog, contFechaReprog);

            }
        });

        alertDialogBuilder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {



                        if(contFechaReprog.getValue() != null ) {
                            //String fechaRep = UtilsAC.formatFechaSimple(contFechaReprog.getValue());
                            String fechaRep = tvFechaVisita.getText().toString();

                            Long idVentaCab = null;

                            //linea modificada
                            ProgramaVisita reprVisita = new ProgramaVisita(null, progVisita.getIdoficial(), fechaRep ,
                                    "", progVisita.getIdcliente(), TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA, idVentaCab ,fechaRep,progVisita.getIdproducto());

                            reprVisita.setEsPrototipoNuevo(true);
                            reprVisita.setEstadoCambiado(true);

                            listaProgrmaVisitas.add(reprVisita);
                        }

                        if(esNecesarioReprogramar(contTipoCl.getValue().getIdtipoclientelog())) {
                            if(contFechaReprog.getValue() == null)
                                throw new IllegalArgumentException("Error: Necesita programar una nueva visita. No tiene fecha de proxima visita");
                        }

                        String obsNoVisita = ((EditText) vista .findViewById(R.id.tfObservacionEstadoVisita)).getText().toString();


                        progVisita.setIdtipoclientelog(contTipoCl.getValue().getIdtipoclientelog());
                        progVisita.setObservacion(obsNoVisita);
                        progVisita.setEstadoCambiado(true);
                        //progVisita.setIdproducto(contArticulo.);
                        listaProgrmaVisitas.add(progVisita);

                        Collections.sort(listaProgrmaVisitas);

                        adapterProgramaVisita.notifyDataSetChanged();

                        if(accionAlAceptarDialogo != null)
                            accionAlAceptarDialogo.realizarAccion();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        // estado de cancelacion de visita


        List<String> estadoList = new ArrayList<String>();

        final Contenedor<String> contEstadoVisita = new Contenedor<>();

        //estadoList.add(ESTADO_VISITA_EXITO);
        estadoList.add(ESTADO_VISITA_NO_EXITO);

        tpoffline.utils.UIBuilder.newDropDownList(vista, R.id.spEstadoVisitaCliente, estadoList, new AccionSeleccionItem<String>() {
            @Override
            public void ejecutarAccion(String eleSelecto) {
                contEstadoVisita.setValue(eleSelecto);
            }
        });

        prepararInputVisitaNoExitosa(vista, true);

        alertDialogBuilder.setView(vista);
        dialogo = alertDialogBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogo.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialogo.getWindow().setAttributes(lp);
        dialogo.show();
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }


    protected boolean activarAceptarSoloConvaloresCambioEstadoReprogramacion(AlertDialog dialogo, TipoClienteLog tipoClienteLog, Contenedor<Date> contFechaReprog) {
        boolean reprogramacionOK = false;

        if(tipoClienteLog.getIdtipoclientelog().longValue() == NullObject.NULL_TIPO_CLIENTE_LOG.getIdtipoclientelog().longValue()) {
            reprogramacionOK = false;
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        } else {
            if(esNecesarioReprogramar(tipoClienteLog.getIdtipoclientelog())) {
                if(contFechaReprog.getValue() != null && UtilsAC.makeSqlDateFromCurrentDate().before(contFechaReprog.getValue())) {
                    dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    reprogramacionOK = true;

                } else {
                    dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    reprogramacionOK = false;
                }
            } else {
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                reprogramacionOK = true;
            }
        }



        return reprogramacionOK;

    }

    protected boolean esNecesarioReprogramar(Long idtipoclientelog) {
        if(idtipoclientelog.longValue() == -1)
            return false;
        else

            return Dao.getTipoClienteLog(context, idtipoclientelog).getReprogramar();
    }

    private boolean esFechaValidaProgramacionFutura(Date fechaProgramacion) {

        return fechaProgramacion != null && UtilsAC.makeSqlDateFromCurrentDate().before(fechaProgramacion);
    }


    private void buildUIcasoNuevaVisita() {

        if(! progVisita.esPrototipoNuevo())
            throw new IllegalStateException("Error progr. debe ser prototipo tipo nuevo");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Visita no exitosa");

        final View vista = LayoutInflater.from(context).inflate(R.layout.layout_dialogo_cambio_estado_programa_visita, null);

        final TextView tvCliente = (TextView) vista.findViewById(R.id.tvCliente);
        final TextView tvArticulo = (TextView) vista.findViewById(R.id.tvArticulo);

        final TextView tvInfoSeleccion = (TextView) vista.findViewById(R.id.tvInfoSeleccion);
        final TextView tvInfoSeleccionArt = (TextView) vista.findViewById(R.id.tvInfoSeleccionArt);

        final EditText tvFechaVisita = (EditText) vista.findViewById(R.id.tvFechaVisita);

        final Contenedor<Cliente> contCliente = new Contenedor<Cliente>();
        final Contenedor<Producto> contArticulo = new Contenedor<Producto>();

        final Contenedor<Date> contFechaVisita = new Contenedor<Date>(UtilsAC.makeSqlDateFromCurrentDate());



        final Contenedor< TipoClienteLog> contTipoCl = new Contenedor<TipoClienteLog>();
        final Contenedor< Date> contFechaReprog = new Contenedor<>();
        final Contenedor<Boolean> fueReprogramado = new Contenedor<Boolean>(false);

        AccionSeleccionItem<Cliente> accionClienteSelect = new AccionSeleccionItem<Cliente>() {
            @Override
            public void ejecutarAccion(Cliente eleSelecto) {
                tvCliente.setText(eleSelecto.toString());
                contCliente.setValue(eleSelecto);

                if(contTipoCl.getValue() != null && contTipoCl.getValue().getIdtipoclientelog().longValue() != -1){
                    if(contFechaVisita.getValue() != null ) {
                        if( esNecesarioReprogramar(contTipoCl.getValue().getIdtipoclientelog().longValue()) ){
                            if(contFechaReprog.getValue() == null) {
                                //tvInfoSeleccion.setText("Selecione una fecha de programacion valida");
                            } else {
                                tvInfoSeleccion.setText("");
                                activarAceptar(dialogo, true);
                            }
                        } else {
                            activarAceptar(dialogo, true);
                            tvInfoSeleccion.setText("");
                        }
                    } else {
                        activarAceptar(dialogo, true);
                    }
                } else {
                    activarAceptar(dialogo, false);
                }
            }
        };





		/*
		AccionSeleccionItem<Articulo> accionArticuloSelect = new AccionSeleccionItem<Articulo>() {
			@Override
			public void ejecutarAccion(Articulo eleSelecto) {
				tvArticulo.setText(eleSelecto.toString());
				contArticulo.setValue(eleSelecto);

				if(contTipoCl.getValue() != null && contTipoCl.getValue().getIdtipoclientelog().longValue() != -1){
					if(contFechaVisita.getValue() != null ) {
						if( esNecesarioReprogramar(contTipoCl.getValue().getIdtipoclientelog().longValue()) ){
							if(contFechaReprog.getValue() == null) {
								//tvInfoSeleccion.setText("Selecione una fecha de programacion valida");
							} else {
								tvInfoSeleccionArt.setText("");
								activarAceptar(dialogo, true);
							}
						} else {
							activarAceptar(dialogo, true);
							tvInfoSeleccionArt.setText("");
						}
					} else {
						activarAceptar(dialogo, true);
					}
				} else {
					activarAceptar(dialogo, false);
				}
			}
		};

		*/










        tpoffline.utils.UIBuilder.nuevoListViewFiltrable(vista,context,  CacheVarios.getListaClientes(context), accionClienteSelect, R.id.lvClienteVisitar, R.id.tfBuscarClienteVisitar);

        final TextView tvBuscarCliente = (TextView)vista.findViewById(R.id.tfBuscarClienteVisitar);
        tvCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvBuscarCliente.setText(" ");
                tvBuscarCliente.requestFocus();
            }
        });

        AccionSeleccionItem<Producto> accionArticuloSelect = new AccionSeleccionItem<Producto>() {

            @Override
            public void ejecutarAccion(Producto eleSelecto) {
                // TODO Auto-generated method stub
                //ArticuloSelecto = eleSelecto;
                //TextView tvArticulo = (TextView)vista.findViewById(R.id.tvArticulo);
                //tvArticulo.setText(eleSelecto.toString());


                tvArticulo.setText(eleSelecto.toString());
                contArticulo.setValue(eleSelecto);



		/*
		if(contTipoCl.getValue() != null && contTipoCl.getValue().getIdtipoclientelog().longValue() != -1){
			if(contFechaVisita.getValue() != null ) {
				if( esNecesarioReprogramar(contTipoCl.getValue().getIdtipoclientelog().longValue()) ){
					if(contFechaReprog.getValue() == null) {
						//tvInfoSeleccion.setText("Selecione una fecha de programacion valida");
					} else {
						tvInfoSeleccionArt.setText("");
						activarAceptar(dialogo, true);
					}
				} else {
					activarAceptar(dialogo, true);
					tvInfoSeleccionArt.setText("");
				}
			} else {
				activarAceptar(dialogo, true);
			}
		} else {
			activarAceptar(dialogo, false);
		}
		*/











            }
        };

        tpoffline.utils.UIBuilder.nuevoListViewFiltrable(vista,context,  Dao.getListaProductos(context), accionArticuloSelect, R.id.lvArticuloVisitar, R.id.tfBuscarArticuloVisitar);

        final TextView tvBuscarArticulo = (TextView)vista.findViewById(R.id.tfBuscarArticuloVisitar);
        tvBuscarArticulo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvBuscarArticulo.setText(" ");
                tvBuscarArticulo.requestFocus();
            }
        });

        //UIBuilder.nuevoListViewFiltrableArticulos(vista,context, CacheVariosArticulos.getListaArticulos(context),, );



        //final EditText tvFechaVisita = (EditText) vista.findViewById(R.id.tvFechaVisita);

        tvCliente.setText("<selecione>");
        tvArticulo.setText("<articulo>");
        tvFechaVisita.setText(UtilsAC.formatFechaSimple(contFechaVisita.getValue()));

        List<TipoClienteLog> listaTipoCliLog = new ArrayList<>();
        listaTipoCliLog.add(NullObject.NULL_TIPO_CLIENTE_LOG);
        listaTipoCliLog.addAll(Dao.getListaTipoClienteLog(context));
        listaTipoCliLog.remove(Dao.getRoDaoSession().getTipoClienteLogDao().load(TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA));

        setAccionBotonReprogramar(vista, "Reprogramar", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tpoffline.widget.UIBuilder.showSelectorFecha(context, new AccionSeleccionItem<Date>() {
                    @Override
                    public void ejecutarAccion(Date fechaSelec) {
                        contFechaReprog.setValue(fechaSelec);

                        if(esNecesarioReprogramar(contTipoCl.getValue().getIdtipoclientelog())) {
                            if(esFechaValidaProgramacionFutura(contFechaReprog.getValue())) {
                                fueReprogramado.setValue(true);
                                activarAceptar(dialogo, true);
                                tvFechaVisita.setText( ""  + fechaSelec.toString());

                            }  else {
                                fueReprogramado.setValue(false);
                                activarAceptar(dialogo, false);
                                tvFechaVisita.setText( "Ingrese una fecha valida");
                            }
                        }
                    }

                });
            }
        });

        UIBuilder.newDropDownList(vista, R.id.spMotivosVisitaNoExitosa, listaTipoCliLog, new AccionSeleccionItem<TipoClienteLog>() {
            @Override
            public void ejecutarAccion(TipoClienteLog tipoClienteLog) {
                contTipoCl.setValue(tipoClienteLog);

                if(contCliente.getValue() == null) {
                    activarAceptar(dialogo, false);
                } else {
                    activarAceptarSoloConvaloresCambioEstadoReprogramacion(dialogo, tipoClienteLog, contFechaVisita);
                }

            }
        });

        List<String> estadoList = new ArrayList<String>();
        estadoList.add(ESTADO_VISITA_NO_EXITO);

        final Spinner spEstadoVisita = (Spinner) vista.findViewById(R.id.spEstadoVisitaCliente);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, estadoList);

        spEstadoVisita.setEnabled(false);
        prepararInputVisitaNoExitosa(vista, true);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoVisita.setAdapter(adapter);

        alertDialogBuilder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(fueReprogramado.getValue()) {
                            //String fechaRep = UtilsAC.formatFechaSimple( contFechaReprog.getValue());
                            String  fechaRep = tvFechaVisita.getText().toString();

                            Long idVentaCab = null;
							/*
							ProgramaVisita visitaRep = new ProgramaVisita(null, SessionUsuario.getUsuarioLogin().getIdusuario(),
									fechaRep ,  "", contCliente.getValue().getIdcliente(), TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA,
									idVentaCab ,fechaRep,ArticuloSelecto.getIdproducto());
							*/

                            ProgramaVisita visitaRep = new ProgramaVisita(null, SessionUsuario.getUsuarioLogin().getIdusuario(),
                                    fechaRep ,  "", contCliente.getValue().getIdcliente(), TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA,
                                    idVentaCab ,fechaRep,contArticulo.getValue().getIdproducto());


                            visitaRep.setEsPrototipoNuevo(true);
                            listaProgrmaVisitas.add(visitaRep);
                        }



                        //final TextView tvFechaVisita = (TextView) vista.findViewById(R.id.tvFechaVisita);
                        String  fechaRep = tvFechaVisita.getText().toString();








                        progVisita.setFechainicio(fechaRep);
                        progVisita.setIdcliente(contCliente.getValue().getIdcliente());
                        progVisita.setIdoficial(SessionUsuario.getUsuarioLogin().getIdusuario());
                        progVisita.setIdtipoclientelog(contTipoCl.getValue().getIdtipoclientelog());



                        String obsNoVisita = ((EditText) vista .findViewById(R.id.tfObservacionEstadoVisita)).getText().toString();

                        //String idProducto = ((EditText) vista .findViewById(R.id.tvArticulo)).getText().toString();
                        //long idProducto2 = Long.parseLong(idProducto);
                        progVisita.setObservacion(obsNoVisita);
                        //progVisita.setIdproducto(idProducto2);


                        listaProgrmaVisitas.add(progVisita);

                        progVisita.setEstadoCambiado(true);
                        //linea agregada para que funcione el seteo del producto
                        progVisita.setIdproducto(contArticulo.getValue().getIdproducto());

                        //progVisita.setIdproducto(contArticulo.getValue().getIdproducto());

                        Collections.sort(listaProgrmaVisitas );

                        adapterProgramaVisita.notifyDataSetChanged();

                        if(accionAlAceptarDialogo != null)
                            accionAlAceptarDialogo.realizarAccion();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        alertDialogBuilder.setView(vista);
        dialogo = alertDialogBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogo.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialogo.getWindow().setAttributes(lp);
        dialogo.show();
        activarAceptar(dialogo, false);

    }


    private void activarAceptar(AlertDialog dialogo, boolean activar) {
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(activar);

    }

    private void setAccionBotonReprogramar(View vista, String botonTexto, View.OnClickListener accionBotonReprogramacion  ) {
        Button bt = (Button) vista.findViewById(R.id.btReprogramar);
        bt.setText(botonTexto);
        bt.setOnClickListener(accionBotonReprogramacion);

    }


    protected void prepararInputVisitaNoExitosa(View vista, boolean prepararParaNoVisita) {


        if (prepararParaNoVisita) {
            vista.findViewById(R.id.llvMotivoNoVisita).setVisibility(
                    View.VISIBLE);
            vista.findViewById(R.id.llMotivoNoVisitaObservacion).setVisibility(
                    View.VISIBLE);
            vista.findViewById(R.id.spMotivosVisitaNoExitosa).setEnabled(true);
            vista.findViewById(R.id.btReprogramar).setVisibility(View.VISIBLE);


        } else {
            vista.findViewById(R.id.spMotivosVisitaNoExitosa).setEnabled(false);
            vista.findViewById(R.id.llvMotivoNoVisita).setVisibility(View.GONE);
            vista.findViewById(R.id.llMotivoNoVisitaObservacion).setVisibility(
                    View.GONE);
            vista.findViewById(R.id.btReprogramar).setVisibility(View.GONE);


        }

    }

    private void  activarAceptarSoloConvalores(AlertDialog dialogo, Contenedor... cv ) {
        boolean activar = true;
        activar  = activar && cv.length > 0;

        for (Contenedor cont : cv) {
            activar = activar && cont != null &&  cont.getValue()!= null;
        }

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(activar);
    }

}









