using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using System.Threading.Tasks;


public static class InterfaceReciever
{
    /// <summary>
    /// Добавляет в качестве компонента нужный класс и возвращает интерфейс работы с ним
    /// </summary>
    public static IBle RecieveIConfForm(GameObject entityForForm)
    {
        var a = entityForForm.AddComponent<Intaracton>().Initialize();
        return a;
    }


    
}





