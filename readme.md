
# Práctica Android Avanzado de Carlos Delgado Andrés

**Madrid Guide** es un prototipo de aplicación para Android 4.0.1 ó superior, realizado con Android Studio 2.2.2.

Se trata de una guía de consulta de comercios y actividades turísitcas de la ciudad de Madrid. Durante su primera ejecución, la app descarga y cachea localmente los datos necesarios (datos sobre las tiendas y actividades, y todas las imágenes que se necesitan, unos 70 MB en total) para poder funcionar posteriormente sin conectarse a internet. Tanto para las tiendas como para las actividades, es posible consultarlas a través de un listado o mediante un mapa que muestra la ubicación geográfica de cada una.

En sucesivos inicios, la aplicación comprobará si ya han transcurrido más de 7 días desde la última descarga de información, en cuyo caso procederá a eliminar todos los datos locales y a descargar una versión más reciente desde el servidor remoto.

La aplicación utiliza las siguientes librerías externas:
- **Gson 2.8.0**: para el parseo de información en formato JSON procedente del servidor.
- **Volley 1.0.0**: para la gestión del envío y recepción de peticiones http.
- **Picasso 2.5.2**: para la gestión de la descarga y cacheo de imágenes localmente.
- **OkHttp3 3.3.0** y **OkHttp3 Downloader 1.0.2**: para la gestión de la caché de disco de Picasso.
- **Butter Knife 8.4.0**: para el enlazado de vistas de los distintos layout.
- **Google Maps Service 10.0.0**: para el fragmento de mapa que muestra las tiendas/actividades cercanas.
- **PhotoView** 1.2.4: para permitir el zoom en las imágenes de mapa estáticas.

.
### Paquetes de la aplicación:

Las clases que componen la aplicación se agrupan en los siguientes paquetes:

##### - **activity**:
Representan los controladores para cada pantalla de la aplicación (*MainActivity*, *ShopsActivity*, *ExperiencesActivity*, *ShopDetailActivity* y *ExperienceDetailActivity*). Existe además una clase adicional *SplashActivity* correspondiente a la Splash screen o pantalla de inicio de la aplicación.

##### - **adapter**:
Contiene las clases adapter que permiten representar objetos del modelo en ciertas vistas de la aplicación como listas de elementos (*ShopsAdapter*, *ExperiencesAdapter*) o ventanas de información correspondientes a marcadores de un mapa (*ShopInfoWindowAdapter*, *ExperienceInfoWindowAdapter*).

##### - **fragment**:
Contiene las clases que controlan los fragmentos correspondientes a los listados de elementos (*ShopListFragment* y *ExperienceListFragment*).

##### - **interactor**:
Contiene las clases que actúan de intermediario entre los controladores y el modelo. Se invocan siempre desde las clases del paquete activity y realizan tareas pesadas en segundo plano, sirviéndose para ello de clases del paquete manager. Todos los objetos de interactor reciben un delegado (listener) que implementa un interfaz concreto para procesar en el hilo principal el resultado de la operación realizada por el interactor en segundo plano.

##### - **manager**:
Contiene clases que realizan operaciones con datos de la aplicación y entidades del modelo, y permiten abstraer a los interactor de la forma concreta en que se implementan las operaciones que realizan. Estas clases manager se han implementado como *singletons*. Este paquete se divide en varios sub-paquetes, en función del tipo de datos que maneja cada manager:

- **db**: compuesto por las clases que manipulan una base de datos SQLite que sirve de caché local para almacenar la información enviada por el servidor. La principal clase del paquete es *DBManager* que gestiona la creación y conexión a la base de datos. Esta clase no es directamente accesible por los interactor, sino por objetos de este mismo paquete que implementan DAOPersistable (*ShopDAO* y *ExperienceDAO*). Cada uno de ellos presenta un interfaz a través del cuál se realizan las operaciones más comunes de acceso a la base de datos para Tiendas y Actividades, respectivamente, evitando tener que utilizar las llamadas a más bajo nivel del DBManager cada vez que debe hacerse una operación de lectura/escritura en la BBDD. Por último, un subpaquete **provider** contiene a la clase *MadridGuideProvider*, un Content Provider que permite acceder a los datos locales de un modo alternativo, mediante URIs y loaders.
    
- **image**: contiene la clase *ImageCacheManager* que se encarga de cachear y hacer disponibles localmente las imágenes de las tiendas y actividades. Internamente utiliza las librerías Picasso y OkHttp3 para la gestión de las cachés de imágenes (tanto en memoria como en disco).
    
- **net**: contiene la clase *NetworkManager* que se encarga del envío de peticiones http al servidor y de parsear las respuestas JSON correspondientes. Internamente utiliza las librerías de Volley y Gson para realizar estas tareas. También incluye métodos para detectar si el dispositivo está conectado a internet y de qué tipo de conexión dispone. También incluye a las clases *ShopsResponse* y *ExperiencesResponse* que modelan las respuestas JSON remotas y que son utilizadas por Gson para parsear los datos recibidos.
    
##### - **model**:
Contiene las clases que representan el modelo de la aplicación (*Shop* y *Experience*). Tambien contiene las clases que actúan como agregados de dichos objetos (*Shops* y *Experiences*), que implementan ambas los protocolos *IterableAggregate* y *UpdatableAggregate*.

Tambien contiene un subpaquete **mapper** con clases auxiliares que permiten transformar las clases (*ShopEntityToShopMapper* y *ExperienceEntityToExperienceMapper*) que modelan una respuesta JSON en sus correspondientes clases del modelo, para su posterior manejo en el resto de la aplicación.

##### - **navigator**:
Contiene la clase *Navigator* que concentra toda la navegación entre activities de la aplicación, lo que permite en todo momento controlar cuándo se pasa de una activity a otra y qué información se va a compartir entre ellas.

##### - **view**:
Agrupa clases auxiliares para el manejo de diferentes vistas, como *ShopRowViewHolder* y *ActivityRowViewHolder* que permiten representar Tiendas y Actividades en las vistas de un RecyclerView. También incluye la clase *OnElementClickedListener* que implementa un interfaz genérico para recibir y procesar las pulsaciones del usuario sobre alguna vista, tales como elementos de un RecyclerView.

##### - **util**:
Contiene la clase *Utils* que contiene funciones auxiliares para el manejo de la UI, las clase *Constants* que almacena variables de configuración de la aplicación y la clase *MainThread*, que simplifica la ejecución de código en el hilo principal desde hilos en segundo plano.

.
### Consideraciones sobre la práctica:

##### - Clase Experience:
Para evitar confusiones con la clase Activity de Android, la clase que representa a cada entidad "actividad" se ha llamado *Experience*.

##### - Claves API:
Se ha omitido del repositorio la clave API para acceder a servicios de **Google Maps para Android**. Para poder compilar el proyecto correctamente, será necesario generar una nueva clave API desde la consola de desarrollador de Google, e incluirla en un nuevo fichero **/res/values/api_keys.xml** con este formato:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_api_key">TU CLAVE API AQUÍ</string>
</resources>
```

##### - Descarga de imágenes remotas:
Al comienzo de la ejecución, la clase *CacheAllImagesInteractor* construye una cola de peticiones para descargar al dispositivo todas las imágenes de las tiendas y actividades. La clase *ImageCacheManager* se encarga de realizar estas peticiones de manera paralela en segundo plano (el límite de descargas simultáneas de Picasso es 3). Cada vez que una petición termina (exitosamente o no), se comprueba si aún quedan otras peticiones pendientes de completarse, y si ya han finalizado todas se devuelve el control al hilo principal.

De este modo se adelanta tiempo realizando las descargas de imágenes simultáneamente para después proseguir con la ejecución síncrona de la aplicación.

**NOTA:** la petición de descarga de actividades del servidor (**http://madrid-shops.com/json_new/getActivities.php**) devuelve una URL de imagen de logotipo incorrecta en la actividad "Museo del Prado", por lo que el logo de esta actividad no se muestra correctamente, pero esto no se debe a ningún problema en la aplicación.

Al finalizar la descarga de imágenes, si alguna de ellas falló se muestra un mensaje de aviso al usuario, pero la ejecución no se interrumpe sino que continúa normalmente. Como durante el resto de la ejecución la aplicación ya no se conecta a internet para descargar más imágenes, en el lugar de aquellas imágenes que hayan fallado se mostrará un placeholder de error.

##### - Ventanas de información de los marcadores del mapa:
Cuando el usuario pulsa en un marcador del mapa, se despliega una ventana de información que muestra tanto el nombre como el logotipo de la entidad pulsada (tienda o actividad). Si se tiene activada la opción de depuración de Picasso (activada por defecto), se observa que dicha imagen del logotipo se muestra en verde (indicando que se ha obtenido de la caché en memoria) y nunca en azul (procedente de la caché de disco).

Esto se debe a que la ventana de información del marcador no es una vista normal (que representa una jerarquía de vistas a las cuales contiene), sino que se trata de una imagen renderizada que representa a las vistas que contiene. Dicha imagen se construye en el momento en que el usuario pulsa el marcador, por lo que para cuando la imagen del logotipo se ha cargado desde disco (es un proceso asíncrono) ya es demasiado tarde para mostrarla, apareciendo en su lugar siempre un placeholder.

Para evitar esto, cuando la imagen a mostrar en la ventana de información se ha cargado correctamente, se invoca al método **refreshInfoWindow()** (ver las clases *InfoWindowAdapter*) que automáticamente oculta y vuelve a renderizar la ventana de información. Como en este caso la imagen ya se encontraba en la caché en memoria, entonces ya aparece visible.

##### - Filtrado de entidades por texto de información de los marcadores del mapa:
Cada una de las ventanas de Tiendas y Actividades dispone de una barra de búsqueda a través de la cuál el usuario puede filtrar los resultados que se muestran en pantalla mediante una cadena de texto.

El criterio para realizar la búsqueda de texto es el siguiente: se buscarán aquellas entidades que contengan la cadena buscada (sin distinción de mayúsculas/minúsculas) en cualquiera de los campos **Nombre**, **Descripción** (solo en el idioma que corresponda) o **Dirección**.

La búsqueda se realiza a través de un **CursorLoader** (el mismo mecanismo que se utiliza para obtener todas las entidades existentes cuando se crea la ventana), al que en este caso se le proporcionan además los datos para poder filtrar.

##### - Layouts distintos según la orientación del dispositivo:
Las actividades ShopsActivity y ExperiencesActivity disponen cada una de dos layouts diferentes, uno para cuando el dispositivo está orientado en modo retrato (mapa arriba y listado abajo) y otro para cuando el dispositivo se gira a modo apaisado (mapa a la izquierda y listado a la derecha).

Para no perder la información sobre la búsqueda que hubiera hecho el usuario ni la posición en el mapa que estaba observando cuando se gira el dispositivo en una u otra posición, se ha implementado en ambas actividades el método **onSaveInstanceState()** para guardar el estado actual en el momento en que una actividad va a ser refrescada. En el método **onCreate()** de cada una de ellas se hace una comprobación para determinar si la actividad se está creando de cero o si está siendo restaurada, en cuyo caso se obtiene el estado salvado del Bundle obtenido y se vuelve a dejar la actividad igual que como estaba justo antes de ser destruida.

En concreto, los datos que se salvaguardan para recuperar el estado de la acticidad son:
- Una lista de las entidades (tiendas, actividades) que se estaba mostrando en esse momento.
- Las coordenadas en que se estaba mostando el mapa, así como su nivel de zoom.

Con dichos datos son suficientes para recoinstruir tanto el listado como el mapa (incluyendo su posición, zoom y marcadores).

##### - Internacionalización:
Tanto la información descargada como los textos en pantalla de la aplicación están disponibles en español y en inglés, seleccionándose automáticamente el primero cuando el idioma del sistema es español, y el segundo cuando se trata de cualquier otro idioma.

Del mismo modo, a la hora de mostrar el detalle de una Tienda o Actividad existen algunos campos que están disponibles en ambos idiomas (tales como la descripción o el horario de apertura). En esos casos, se determina en tiempo de ejecución cuál es el valor que se debe mostrar. Tanto la clase Shop como la clase Experience disponen de métodos **getLocalizedDescription()** y **getLocalizedOpeningHours()** que devuelven la opción adecuada para cada caso.